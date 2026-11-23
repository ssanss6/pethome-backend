package com.pethome.vo;

import com.pethome.entity.OrderStatusLog;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 寄养订单详情 VO（含宠物/房型/笼位名称 + 状态时间线）
 */
@Data
public class BoardingOrderVO {

    private Long id;

    private String orderNo;

    private Long petId;

    private String petName;

    private Long roomTypeId;

    private String roomTypeName;

    private Long cageId;

    private String cageNo;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer nights;

    private String status;

    private String statusDesc;

    private BigDecimal baseAmount;

    private BigDecimal depositAmount;

    private BigDecimal extraAmount;

    private BigDecimal timeoutFee;

    private String remark;

    private LocalDateTime createdAt;

    private List<OrderStatusLog> statusLogs;
}
