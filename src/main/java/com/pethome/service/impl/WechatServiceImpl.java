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
 * 微信小程序登录服务实现
 */
@Service
@RequiredArgsConstructor
public class WechatServiceImpl implements WechatService {

    private final WxProperties properties;

    private static final String CODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /**
     * 用 wx.login 的 code 换取 openid 等会话信息
     *
     * @param code 临时登录凭证
     * @return 会话信息（openid / sessionKey / unionid）
     */
    @Override
    public WechatSession code2session(String code) {
        // 本地开发 mock 模式：不请求真实微信接口，直接用 code 拼一个假 openid
        if (properties.isMockEnabled()) {
            WechatSession session = new WechatSession();
            session.setOpenid("mock_" + code);
            return session;
        }

        //1、请求微信 code2session 接口
        String url = String.format(CODE2SESSION_URL, properties.getAppId(), properties.getSecret(), code);
        String body = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(body);

        //2、校验返回结果，errcode 非 0 表示失败
        Integer errcode = json.getInt("errcode", 0);
        if (errcode != null && errcode != 0) {
            throw new BizException(ErrorCode.WX_LOGIN_FAILED, "微信登录失败：" + json.getStr("errmsg"));
        }

        //3、封装会话信息返回
        WechatSession session = new WechatSession();
        session.setOpenid(json.getStr("openid"));
        session.setSessionKey(json.getStr("session_key"));
        session.setUnionid(json.getStr("unionid"));
        return session;
    }
}
