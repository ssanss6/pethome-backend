package com.pethome.enums;

import lombok.Getter;

/**
 * 售后类型
 */
@Getter
public enum AfterSaleType {

    REFUND("REFUND", "退款"),
    COMPLAINT("COMPLAINT", "投诉");

    private final String code;
    private final String desc;

    AfterSaleType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
