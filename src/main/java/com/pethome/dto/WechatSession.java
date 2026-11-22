package com.pethome.dto;

import lombok.Data;

/**
 * 微信 code2session 会话信息
 */
@Data
public class WechatSession {

    private String openid;

    private String sessionKey;

    private String unionid;
}
