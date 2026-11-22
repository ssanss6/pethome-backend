package com.pethome.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.entity.Cage;
import com.pethome.entity.RoomType;
import com.pethome.entity.Store;
import com.pethome.entity.SysRole;
import com.pethome.entity.SysUser;
import com.pethome.entity.SysUserRole;
import com.pethome.enums.RoleEnum;
import com.pethome.enums.UserType;
import com.pethome.mapper.CageMapper;
import com.pethome.mapper.RoomTypeMapper;
import com.pethome.mapper.StoreMapper;
import com.pethome.mapper.SysRoleMapper;
import com.pethome.mapper.SysUserMapper;
import com.pethome.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 首次启动种子数据：角色 + 默认账号 + 门店 + 房型 + 笼位
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final StoreMapper storeMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final CageMapper cageMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Map<String, SysRole> roles = seedRoles();
        seedAdmin(roles);
        seedEmployee(roles);
        seedStore();
        seedRoomTypes();
        seedCages();
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

    private void seedStore() {
        if (storeMapper.selectCount(null) > 0) {
            return;
        }
        Store store = new Store();
        store.setName("毛孩子宠物生活馆");
        store.setAddress("北京 · 朝阳区望京 SOHO T3");
        store.setPhone("010-88888888");
        store.setBusinessHours("09:00–21:00");
        store.setStatus(1);
        storeMapper.insert(store);
        log.info("已初始化默认门店");
    }

    private void seedRoomTypes() {
        insertRoomTypeIfAbsent("标准间", "2.0m × 1.5m", new BigDecimal("88.00"), "独立通风 · 每日遛弯");
        insertRoomTypeIfAbsent("豪华间", "3.0m × 2.0m", new BigDecimal("138.00"), "监控直播 · 每日遛弯");
        insertRoomTypeIfAbsent("猫咪房", "静音区", new BigDecimal("108.00"), "猫爬架 · 独立猫砂盆");
    }

    private void insertRoomTypeIfAbsent(String name, String size, BigDecimal price, String desc) {
        Long count = roomTypeMapper.selectCount(
                new LambdaQueryWrapper<RoomType>().eq(RoomType::getName, name));
        if (count == 0) {
            RoomType roomType = new RoomType();
            roomType.setStoreId(1L);
            roomType.setName(name);
            roomType.setSize(size);
            roomType.setDailyPrice(price);
            roomType.setDescription(desc);
            roomType.setStatus(1);
            roomTypeMapper.insert(roomType);
        }
    }

    private void seedCages() {
        insertCageIfAbsent("标准间", "A01");
        insertCageIfAbsent("标准间", "A03");
        insertCageIfAbsent("豪华间", "B01");
        insertCageIfAbsent("猫咪房", "C02");
        insertCageIfAbsent("猫咪房", "D01");
    }

    private void insertCageIfAbsent(String roomTypeName, String cageNo) {
        RoomType roomType = roomTypeMapper.selectOne(
                new LambdaQueryWrapper<RoomType>().eq(RoomType::getName, roomTypeName));
        if (roomType == null) {
            return;
        }
        Long count = cageMapper.selectCount(
                new LambdaQueryWrapper<Cage>()
                        .eq(Cage::getStoreId, 1L)
                        .eq(Cage::getCageNo, cageNo));
        if (count == 0) {
            Cage cage = new Cage();
            cage.setStoreId(1L);
            cage.setRoomTypeId(roomType.getId());
            cage.setCageNo(cageNo);
            cage.setStatus(1);
            cageMapper.insert(cage);
        }
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
