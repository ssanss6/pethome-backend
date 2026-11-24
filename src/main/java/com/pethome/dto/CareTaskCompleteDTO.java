package com.pethome.dto;

import lombok.Data;

/**
 * 照护任务打卡入参
 */
@Data
public class CareTaskCompleteDTO {

    /** 打卡照片 URL（可选） */
    private String photoUrl;

    /** 备注（可选） */
    private String note;
}
