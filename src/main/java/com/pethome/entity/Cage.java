package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 笼位表
 */
@Data
@TableName("cage")
public class Cage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long storeId;

    private Long roomTypeId;

    private String cageNo;

    /** 1启用 2停用 3维修 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
