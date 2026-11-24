package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员次卡
 */
@Data
@TableName("membership_card")
public class MembershipCard {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ownerId;

    private Long serviceItemId;

    private Integer totalTimes;

    private Integer usedTimes;

    private LocalDate expireDate;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
