package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 售后工单
 */
@Data
@TableName("after_sale")
public class AfterSale {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** BOARDING/GROOMING */
    private String orderType;

    private Long orderId;

    private Long ownerId;

    /** REFUND/COMPLAINT */
    private String type;

    private String reason;

    /** OPEN/PROCESSING/RESOLVED/CLOSED */
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
