package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 门店表
 */
@Data
@TableName("store")
public class Store {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String address;

    private String phone;

    private String businessHours;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
