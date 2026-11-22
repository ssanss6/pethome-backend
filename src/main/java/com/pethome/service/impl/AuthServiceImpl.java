package com.pethome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
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
import com.pethome.service.WechatService;
import com.pethome.vo.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final WechatService wechatService;

    
    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
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
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.PASSWORD_ERROR);
        }
        List<String> roles = loadRoles(user.getId());
        String token = jwtUtil.generate(
                new LoginUser(user.getId(), user.getUserType(), roles, null, user.getUsername()));
        return buildResponse(user, roles, token);
    }

    @Override
    @Transactional
    public LoginResponse wxLogin(WxLoginRequest request) {
        WechatSession session = wechatService.code2session(request.getCode());
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
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(ErrorCode.USER_DISABLED);
        }
        List<String> roles = loadRoles(user.getId());
        String token = jwtUtil.generate(
                new LoginUser(user.getId(), user.getUserType(), roles, user.getOpenid(), null));
        return buildResponse(user, roles, token);
    }

    private List<String> loadRoles(Long userId) {
        List<Long> roleIds = userRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream()
                .map(SysUserRole::getRoleId)
                .toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return roleMapper.selectByIds(roleIds).stream()
                .map(SysRole::getRoleCode)
                .toList();
    }

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
