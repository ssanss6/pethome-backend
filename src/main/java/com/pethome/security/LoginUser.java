package com.pethome.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 登录主体（写入 JWT / 存入上下文）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {

    private Long userId;

    /** 1宠物主 2员工 3管理员 */
    private Integer userType;

    /** 角色编码列表 */
    private List<String> roles;

    /** 微信 openid（微信登录时有值） */
    private String openid;

    /** 登录名（账号密码登录时有值） */
    private String username;
}
