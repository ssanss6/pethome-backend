package com.pethome.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云 OSS 配置
 */
@Data
@ConfigurationProperties(prefix = "pethome.oss")
public class OssProperties {

    /** 地域节点，如 https://oss-cn-hangzhou.aliyuncs.com */
    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucket;

    /** 可选：自定义域名/CDN 前缀，为空则用 endpoint 拼接 */
    private String domain;

    /** 签名 URL 过期时长（秒） */
    private long signExpireSeconds = 600;
}
