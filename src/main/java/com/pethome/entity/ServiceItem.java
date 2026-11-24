package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 洗护服务项目
 */
@Data
@TableName("service_item")
public class ServiceItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long storeId;

    private String name;

    private BigDecimal price;

    private Integer durationMinutes;

    private String description;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
