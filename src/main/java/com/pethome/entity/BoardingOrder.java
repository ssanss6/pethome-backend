package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 寄养订单表
 */
@Data
@TableName("boarding_order")
public class BoardingOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    /** 宠物主用户ID */
    private Long ownerId;

    private Long petId;

    private Long storeId;

    private Long roomTypeId;

    /** 分配的笼位（门店确认时锁定） */
    private Long cageId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    /** 晚数 */
    private Integer nights;

    /** 订单状态（PENDING_PAY 等） */
    private String status;

    private BigDecimal baseAmount;

    private BigDecimal depositAmount;

    private BigDecimal extraAmount;

    private BigDecimal timeoutFee;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
