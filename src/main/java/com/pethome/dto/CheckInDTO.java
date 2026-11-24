package com.pethome.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 入住登记入参
 */
@Data
public class CheckInDTO {

    /** 押金（可选） */
    private BigDecimal depositAmount;
}
