package com.pethome.enums;

import lombok.Getter;

/**
 * 角色枚举（RBAC）
 */
@Getter
public enum RoleEnum {

    PET_OWNER("PET_OWNER", "宠物主"),
    CAREGIVER("CAREGIVER", "照护员"),
    GROOMER("GROOMER", "美容师"),
    RECEPTION("RECEPTION", "前台"),
    STORE_MANAGER("STORE_MANAGER", "店长"),
    PLATFORM_ADMIN("PLATFORM_ADMIN", "平台管理员");

    private final String code;
    private final String name;

    RoleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
