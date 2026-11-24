package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价
 */
@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** BOARDING/GROOMING */
    private String orderType;

    private Long orderId;

    private Long ownerId;

    private Integer rating;

    private String content;

    private LocalDateTime createdAt;
}
