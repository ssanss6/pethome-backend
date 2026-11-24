package com.pethome.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 次卡购买入参
 */
@Data
public class MembershipCardCreateDTO {

    private Long serviceItemId;

    private Integer totalTimes;

    private LocalDate expireDate;
}
