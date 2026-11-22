package com.pethome.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 笼位 新增/编辑入参
 */
@Data
public class CageDTO {

    @NotNull(message = "房型不能为空")
    private Long roomTypeId;

    @NotBlank(message = "笼位号不能为空")
    private String cageNo;

    /** 1启用 2停用 3维修 */
    private Integer status;
}
