package com.pethome.service.storage.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.config.OssProperties;
import com.pethome.service.storage.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 阿里云 OSS 服务：服务端签名 URL，前端直传，不暴露 AccessKey
 */
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final OSS ossClient;
    private final OssProperties properties;

    @Override
    public String generateAccessUrl(String objectKey) {
        try {
            Date expire = new Date(System.currentTimeMillis() + properties.getSignExpireSeconds() * 1000L);
            URL url = ossClient.generatePresignedUrl(properties.getBucket(), objectKey, expire, HttpMethod.GET);
            return url.toString();
        } catch (Exception e) {
            throw new BizException(ErrorCode.OSS_ERROR, "生成访问签名失败");
        }
    }

    @Override
    public String generateUploadUrl(String objectKey) {
        try {
            Date expire = new Date(System.currentTimeMillis() + properties.getSignExpireSeconds() * 1000L);
            URL url = ossClient.generatePresignedUrl(properties.getBucket(), objectKey, expire, HttpMethod.PUT);
            return url.toString();
        } catch (Exception e) {
            throw new BizException(ErrorCode.OSS_ERROR, "生成上传签名失败");
        }
    }

    @Override
    public String selfTest() {
        String key = "_check/connectivity-" + System.currentTimeMillis() + ".txt";
        String content = "pethome-oss-ok";
        try {
            ossClient.putObject(properties.getBucket(), key,
                    new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            OSSObject obj = ossClient.getObject(properties.getBucket(), key);
            String read = new String(obj.getObjectContent().readAllBytes(), StandardCharsets.UTF_8);
            obj.close();
            ossClient.deleteObject(properties.getBucket(), key);
            return content.equals(read) ? ("OK: " + read) : ("MISMATCH: " + read);
        } catch (Exception e) {
            throw new BizException(ErrorCode.OSS_ERROR, "OSS 自检失败：" + e.getMessage());
        }
    }
}
