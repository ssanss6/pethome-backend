package com.pethome.enums;

import lombok.Getter;

/**
 * 售后状态
 */
@Getter
public enum AfterSaleStatus {

    OPEN("OPEN", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    RESOLVED("RESOLVED", "已解决"),
    CLOSED("CLOSED", "已关闭");

    private final String code;
    private final String desc;

    AfterSaleStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
