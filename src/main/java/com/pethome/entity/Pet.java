package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宠物档案表
 */
@Data
@TableName("pet")
public class Pet {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 宠物主用户ID */
    private Long ownerId;

    private String name;

    /** 犬/猫/其他 */
    private String species;

    private String breed;

    /** 0未知 1公 2母 */
    private Integer gender;

    private LocalDate birthDate;

    private BigDecimal weight;

    private String avatarUrl;

    private LocalDate vaccineExpireDate;

    private String allergyNote;

    /** 1正常 0删除 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
