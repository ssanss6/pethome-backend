package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 洗护订单
 */
@Data
@TableName("grooming_order")
public class GroomingOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long ownerId;

    private Long petId;

    private Long storeId;

    private Long serviceItemId;

    private Long groomerId;

    private LocalDate serviceDate;

    /** 时段，如 09:00 */
    private String slot;

    private String status;

    private BigDecimal amount;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
