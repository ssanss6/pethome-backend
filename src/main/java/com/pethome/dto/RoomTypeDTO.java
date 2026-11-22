package com.pethome.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房型 新增/编辑入参
 */
@Data
public class RoomTypeDTO {

    @NotBlank(message = "房型名不能为空")
    private String name;

    private String size;

    @NotNull(message = "每日价格不能为空")
    private BigDecimal dailyPrice;

    private String description;
}
