package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房型表
 */
@Data
@TableName("room_type")
public class RoomType {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long storeId;

    private String name;

    private String size;

    private BigDecimal dailyPrice;

    private String description;

    /** 1启用 0停用 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
