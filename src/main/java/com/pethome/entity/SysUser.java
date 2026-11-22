package com.pethome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录名（员工/管理员） */
    private String username;

    /** BCrypt 密码（微信用户为空） */
    private String password;

    /** 微信 openid（宠物主） */
    private String openid;

    private String unionid;

    private String nickname;

    private String phone;

    private String avatarUrl;

    /** 1宠物主 2员工 3管理员 */
    private Integer userType;

    /** 1启用 0禁用 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
