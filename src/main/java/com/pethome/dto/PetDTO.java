package com.pethome.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 宠物档案 新增/编辑入参
 */
@Data
public class PetDTO {

    @NotBlank(message = "宠物名不能为空")
    private String name;

    private String species;

    private String breed;

    /** 0未知 1公 2母 */
    private Integer gender;

    private LocalDate birthDate;

    private BigDecimal weight;

    private String avatarUrl;

    private LocalDate vaccineExpireDate;

    private String allergyNote;
}
