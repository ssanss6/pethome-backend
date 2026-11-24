package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 离店结算单
 */
@Data
@TableName("settlement")
public class Settlement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String settlementNo;

    private Long boardingOrderId;

    private BigDecimal baseAmount;

    private BigDecimal extraAmount;

    private BigDecimal timeoutFee;

    private BigDecimal depositAmount;

    private BigDecimal totalAmount;

    /** PENDING/SETTLED */
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
