package com.pethome.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 照护任务 VO（含宠物/笼位/房型名称）
 */
@Data
public class CareTaskVO {

    private Long id;

    private Long boardingOrderId;

    private String orderNo;

    private String petName;

    private String cageNo;

    private String roomTypeName;

    private LocalDate taskDate;

    private String taskType;

    private String taskTypeDesc;

    private String status;

    private String statusDesc;

    private LocalDateTime doneAt;

    private String photoUrl;

    private String note;
}
