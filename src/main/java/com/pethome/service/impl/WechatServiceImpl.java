package com.pethome.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.config.WxProperties;
import com.pethome.dto.WechatSession;
import com.pethome.service.WechatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 微信小程序登录服务
 */
@Service
@RequiredArgsConstructor
public class WechatServiceImpl implements WechatService {

    private final WxProperties properties;

    private static final String CODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    @Override
    public WechatSession code2session(String code) {
        // 本地开发 mock：不请求真实微信接口
        if (properties.isMockEnabled()) {
            WechatSession session = new WechatSession();
            session.setOpenid("mock_" + code);
            return session;
        }

        String url = String.format(CODE2SESSION_URL, properties.getAppId(), properties.getSecret(), code);
        String body = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(body);

        Integer errcode = json.getInt("errcode", 0);
        if (errcode != null && errcode != 0) {
            throw new BizException(ErrorCode.WX_LOGIN_FAILED, "微信登录失败：" + json.getStr("errmsg"));
        }

        WechatSession session = new WechatSession();
        session.setOpenid(json.getStr("openid"));
        session.setSessionKey(json.getStr("session_key"));
        session.setUnionid(json.getStr("unionid"));
        return session;
    }
}
