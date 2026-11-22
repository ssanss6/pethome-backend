package com.pethome.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录入参（宠物主）
 */
@Data
public class WxLoginRequest {

    /** wx.login() 返回的临时 code */
    @NotBlank(message = "code 不能为空")
    private String code;

    /** 昵称（可选，用户填写后传入，首次注册时回填） */
    private String nickname;
}
