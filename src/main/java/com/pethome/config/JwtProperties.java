package com.pethome.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置
 */
@Data
@ConfigurationProperties(prefix = "pethome.jwt")
public class JwtProperties {

    /** 签名密钥（HS256，长度需 >= 32 字节） */
    private String secret;

    /** 过期时间（分钟） */
    private long expireMinutes = 10080;
}
