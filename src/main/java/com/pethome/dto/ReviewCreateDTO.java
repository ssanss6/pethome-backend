package com.pethome.dto;

import lombok.Data;

/**
 * 评价入参
 */
@Data
public class ReviewCreateDTO {

    /** BOARDING/GROOMING */
    private String orderType;

    private Long orderId;

    private Integer rating;

    private String content;
}
