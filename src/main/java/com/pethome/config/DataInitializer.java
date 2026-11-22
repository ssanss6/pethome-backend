package com.pethome.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.entity.SysRole;
import com.pethome.entity.SysUser;
import com.pethome.entity.SysUserRole;
import com.pethome.enums.RoleEnum;
import com.pethome.enums.UserType;
import com.pethome.mapper.SysRoleMapper;
import com.pethome.mapper.SysUserMapper;
import com.pethome.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 首次启动种子数据：角色 + 默认管理员/员工账号
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Map<String, SysRole> roles = seedRoles();
        seedAdmin(roles);
        seedEmployee(roles);
    }

    private Map<String, SysRole> seedRoles() {
        Map<String, SysRole> map = new HashMap<>();
        for (RoleEnum r : RoleEnum.values()) {
            SysRole role = roleMapper.selectOne(
                    new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, r.getCode()));
            if (role == null) {
                role = new SysRole();
                role.setRoleCode(r.getCode());
                role.setRoleName(r.getName());
                roleMapper.insert(role);
            }
            map.put(r.getCode(), role);
        }
        return map;
    }

    private void seedAdmin(Map<String, SysRole> roles) {
        if (existsUsername("admin")) {
            return;
        }
        SysUser admin = new SysUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setNickname("超级管理员");
        admin.setUserType(UserType.ADMIN.getCode());
        admin.setStatus(1);
        userMapper.insert(admin);
        bindRole(admin.getId(), roles.get(RoleEnum.PLATFORM_ADMIN.getCode()));
        bindRole(admin.getId(), roles.get(RoleEnum.STORE_MANAGER.getCode()));
        log.info("已初始化管理员账号 admin / admin123");
    }

    private void seedEmployee(Map<String, SysRole> roles) {
        if (existsUsername("staff01")) {
            return;
        }
        SysUser staff = new SysUser();
        staff.setUsername("staff01");
        staff.setPassword(passwordEncoder.encode("123456"));
        staff.setNickname("演示照护员");
        staff.setUserType(UserType.EMPLOYEE.getCode());
        staff.setStatus(1);
        userMapper.insert(staff);
        bindRole(staff.getId(), roles.get(RoleEnum.CAREGIVER.getCode()));
        log.info("已初始化员工账号 staff01 / 123456");
    }

    private boolean existsUsername(String username) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)) > 0;
    }

    private void bindRole(Long userId, SysRole role) {
        if (role == null) {
            return;
        }
        SysUserRole ur = new SysUserRole();
        ur.setUserId(userId);
        ur.setRoleId(role.getId());
        userRoleMapper.insert(ur);
    }
}
