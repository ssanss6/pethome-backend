package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 美容师
 */
@Data
@TableName("groomer")
public class Groomer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long storeId;

    private Long userId;

    private String name;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
