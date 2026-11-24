package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日照护任务表
 */
@Data
@TableName("care_task")
public class CareTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long boardingOrderId;

    private LocalDate taskDate;

    /** FEED/WALK/CLEAN/PHOTO */
    private String taskType;

    /** PENDING/DONE */
    private String status;

    /** 打卡员工ID */
    private Long operatorId;

    private LocalDateTime doneAt;

    private String photoUrl;

    private String note;

    private LocalDateTime createdAt;
}
