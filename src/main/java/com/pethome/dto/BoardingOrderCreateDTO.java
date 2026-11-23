package com.pethome.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 寄养订单创建入参
 */
@Data
public class BoardingOrderCreateDTO {

    @NotNull(message = "宠物不能为空")
    private Long petId;

    @NotNull(message = "房型不能为空")
    private Long roomTypeId;

    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;

    @NotNull(message = "离店日期不能为空")
    private LocalDate checkOutDate;

    private String remark;
}
