package com.pethome.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 洗护服务项目入参
 */
@Data
public class ServiceItemDTO {

    private String name;

    private BigDecimal price;

    private Integer durationMinutes;

    private String description;
}
