package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单状态流转日志表
 */
@Data
@TableName("order_status_log")
public class OrderStatusLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单类型 BOARDING/GROOMING */
    private String orderType;

    private Long orderId;

    private String fromStatus;

    private String toStatus;

    private Long operatorId;

    private String reason;

    private LocalDateTime createdAt;
}
