package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 笼位按天占用表（区间库存核心）
 */
@Data
@TableName("cage_occupancy")
public class CageOccupancy {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long cageId;

    /** 占用日期 */
    private LocalDate occupyDate;

    /** 寄养订单ID */
    private Long boardingOrderId;
}
