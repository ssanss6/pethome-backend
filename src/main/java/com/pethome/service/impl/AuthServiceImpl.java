package com.pethome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.config.JwtProperties;
import com.pethome.dto.LoginRequest;
import com.pethome.dto.WechatSession;
import com.pethome.dto.WxLoginRequest;
import com.pethome.entity.SysRole;
import com.pethome.entity.SysUser;
import com.pethome.entity.SysUserRole;
import com.pethome.enums.RoleEnum;
import com.pethome.enums.UserType;
import com.pethome.mapper.SysRoleMapper;
import com.pethome.mapper.SysUserMapper;
import com.pethome.mapper.SysUserRoleMapper;
import com.pethome.security.JwtUtil;
import com.pethome.security.LoginUser;
import com.pethome.service.AuthService;
import com.pethome.service.RedisService;
import com.pethome.service.WechatService;
import com.pethome.vo.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final WechatService wechatService;
    private final RedisService redisService;
    private final JwtProperties jwtProperties;

    /**
     * 员工/管理员账号密码登录
     *
     * @param request 登录请求（username + password）
     * @return 登录响应（token + 用户信息）
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        //1、按用户名查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));

        //2、处理各种异常情况（用户不存在、宠物主走微信、账号禁用、密码错误）
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        // 密码登录仅限员工/管理员；宠物主走微信登录
        if (user.getUserType() == null || user.getUserType() == UserType.PET_OWNER.getCode()) {
            throw new BizException(ErrorCode.PASSWORD_ERROR, "该账号请使用微信登录");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(ErrorCode.USER_DISABLED);
        }
        // 密码比对：BCrypt 密文比对
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.PASSWORD_ERROR);
        }

        //3、加载角色并生成 JWT，登录态写入 Redis
        List<String> roles = loadRoles(user.getId());
        String token = jwtUtil.generate(
                new LoginUser(user.getId(), user.getUserType(), roles, null, user.getUsername()));
        storeToken(token, user.getId());
        return buildResponse(user, roles, token);
    }

    /**
     * 宠物主微信登录（首次自动注册）
     *
     * @param request 微信登录请求（code + 可选昵称）
     * @return 登录响应
     */
    @Override
    @Transactional
    public LoginResponse wxLogin(WxLoginRequest request) {
        //1、用 code 换 openid
        WechatSession session = wechatService.code2session(request.getCode());

        //2、按 openid 查询用户，不存在则自动注册
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenid, session.getOpenid()));
        if (user == null) {
            // 首次微信登录：自动注册宠物主账号，昵称优先用用户填写的
            user = new SysUser();
            user.setOpenid(session.getOpenid());
            user.setUnionid(session.getUnionid());
            user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : "微信用户");
            user.setUserType(UserType.PET_OWNER.getCode());
            user.setStatus(1);
            userMapper.insert(user);
            bindRole(user.getId(), RoleEnum.PET_OWNER);
        } else if (StringUtils.hasText(request.getNickname()) && !request.getNickname().equals(user.getNickname())) {
            // 已注册但补填昵称
            user.setNickname(request.getNickname());
            userMapper.updateById(user);
        }

        // 账号被禁用则不允许登录
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(ErrorCode.USER_DISABLED);
        }

        //3、加载角色并生成 JWT，登录态写入 Redis
        List<String> roles = loadRoles(user.getId());
        String token = jwtUtil.generate(
                new LoginUser(user.getId(), user.getUserType(), roles, user.getOpenid(), null));
        storeToken(token, user.getId());
        return buildResponse(user, roles, token);
    }

    /**
     * 退出登录（删除 Redis 登录态，token 立即失效）
     *
     * @param token 登录 token
     */
    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            try {
                redisService.delete("login:token:" + token);
            } catch (Exception e) {
                log.warn("退出登录清理 Redis 失败：{}", e.getMessage());
            }
        }
    }

    /** 登录态写入 Redis，TTL 与 JWT 有效期一致（Redis 不可用时降级为无状态 JWT，不影响登录） */
    private void storeToken(String token, Long userId) {
        try {
            redisService.set("login:token:" + token, String.valueOf(userId),
                    jwtProperties.getExpireMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("登录态写入 Redis 失败，降级为无状态 JWT：{}", e.getMessage());
        }
    }

    /**
     * 加载用户角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    private List<String> loadRoles(Long userId) {
        // 查用户角色关联表拿角色ID
        List<Long> roleIds = userRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream()
                .map(SysUserRole::getRoleId)
                .toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        // 批量查角色表拿角色编码
        return roleMapper.selectByIds(roleIds).stream()
                .map(SysRole::getRoleCode)
                .toList();
    }

    /**
     * 给用户绑定角色
     *
     * @param userId 用户ID
     * @param role   角色枚举
     */
    private void bindRole(Long userId, RoleEnum role) {
        SysRole sysRole = roleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, role.getCode()));
        if (sysRole == null) {
            return;
        }
        SysUserRole ur = new SysUserRole();
        ur.setUserId(userId);
        ur.setRoleId(sysRole.getId());
        userRoleMapper.insert(ur);
    }

    /**
     * 组装登录响应
     *
     * @param user  用户实体
     * @param roles 角色编码列表
     * @param token JWT
     * @return 登录响应
     */
    private LoginResponse buildResponse(SysUser user, List<String> roles, String token) {
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserId(user.getId());
        resp.setUserType(user.getUserType());
        resp.setRoles(roles);
        resp.setNickname(user.getNickname());
        resp.setUsername(user.getUsername());
        resp.setOpenid(user.getOpenid());
        resp.setPhone(user.getPhone());
        resp.setAvatarUrl(user.getAvatarUrl());
        return resp;
    }
}
