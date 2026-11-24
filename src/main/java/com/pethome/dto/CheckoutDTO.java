package com.pethome.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 离店结算入参
 */
@Data
public class CheckoutDTO {

    /** 加项金额 */
    private BigDecimal extraAmount;

    /** 超时费 */
    private BigDecimal timeoutFee;
}
