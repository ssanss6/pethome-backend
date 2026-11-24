package com.pethome.enums;

import lombok.Getter;

/**
 * 照护任务类型
 */
@Getter
public enum CareTaskType {

    FEED("FEED", "喂食"),
    WALK("WALK", "遛弯"),
    CLEAN("CLEAN", "清洁"),
    PHOTO("PHOTO", "拍照");

    private final String code;
    private final String desc;

    CareTaskType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
