package com.pethome.service;

import com.pethome.dto.WechatSession;

public interface WechatService {

    /** 用 wx.login 的 code 换取 openid 等会话信息 */
    WechatSession code2session(String code);
}
