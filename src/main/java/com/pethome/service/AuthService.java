package com.pethome.service;

import com.pethome.dto.LoginRequest;
import com.pethome.dto.WxLoginRequest;
import com.pethome.vo.LoginResponse;

public interface AuthService {

    /** 员工/管理员账号密码登录 */
    LoginResponse login(LoginRequest request);

    /** 宠物主微信登录 */
    LoginResponse wxLogin(WxLoginRequest request);
}
