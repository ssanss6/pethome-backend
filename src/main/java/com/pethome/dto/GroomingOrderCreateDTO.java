package com.pethome.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 洗护预约入参
 */
@Data
public class GroomingOrderCreateDTO {

    private Long petId;

    private Long serviceItemId;

    private Long groomerId;

    private LocalDate serviceDate;

    /** 时段，如 09:00 */
    private String slot;

    private String remark;
}
