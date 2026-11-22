package com.pethome.enums;

import lombok.Getter;

/**
 * 用户类型
 */
@Getter
public enum UserType {

    PET_OWNER(1, "宠物主"),
    EMPLOYEE(2, "员工"),
    ADMIN(3, "管理员");

    private final int code;
    private final String desc;

    UserType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
