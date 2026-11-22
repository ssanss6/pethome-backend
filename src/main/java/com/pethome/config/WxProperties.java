package com.pethome.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信小程序登录配置
 */
@Data
@ConfigurationProperties(prefix = "pethome.wx")
public class WxProperties {

    private String appId;

    private String secret;

    /** true 时本地开发走 mock openid，不请求真实微信接口 */
    private boolean mockEnabled = false;
}
