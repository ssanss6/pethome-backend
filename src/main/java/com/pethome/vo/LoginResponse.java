package com.pethome.vo;

import lombok.Data;

import java.util.List;

/**
 * 登录响应
 */
@Data
public class LoginResponse {

    private String token;

    private Long userId;

    /** 1宠物主 2员工 3管理员 */
    private Integer userType;

    private List<String> roles;

    private String nickname;

    private String username;

    private String openid;

    private String phone;

    private String avatarUrl;
}
