package com.pethome.dto;

import lombok.Data;

/**
 * 售后提交入参
 */
@Data
public class AfterSaleCreateDTO {

    /** BOARDING/GROOMING */
    private String orderType;

    private Long orderId;

    /** REFUND/COMPLAINT */
    private String type;

    private String reason;
}
