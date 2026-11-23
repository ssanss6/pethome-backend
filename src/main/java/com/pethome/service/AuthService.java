package com.pethome.service;

import com.pethome.dto.LoginRequest;
import com.pethome.dto.WxLoginRequest;
import com.pethome.vo.LoginResponse;

public interface AuthService {

    /** 员工/管理员账号密码登录 */
    LoginResponse login(LoginRequest request);

    /** 宠物主微信登录 */
    LoginResponse wxLogin(WxLoginRequest request);

    /** 退出登录（删除 Redis 登录态） */
    void logout(String token);
}
