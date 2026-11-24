package com.pethome.enums;

import lombok.Getter;

/**
 * 照护任务状态
 */
@Getter
public enum CareTaskStatus {

    PENDING("PENDING", "待打卡"),
    DONE("DONE", "已完成");

    private final String code;
    private final String desc;

    CareTaskStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
