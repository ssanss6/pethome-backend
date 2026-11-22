package com.pethome.service.storage.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.config.OssProperties;
import com.pethome.service.storage.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 阿里云 OSS 服务：服务端签名 URL + 直传，不暴露 AccessKey
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

    @Override
    public String upload(String objectKey, MultipartFile file) {
        try {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(file.getContentType());
            meta.setContentLength(file.getSize());
            ossClient.putObject(properties.getBucket(), objectKey, file.getInputStream(), meta);
            // 返回 7 天有效的访问签名 URL（图片长期展示用；生产建议公共读或按需刷新签名）
            Date expire = new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000);
            URL url = ossClient.generatePresignedUrl(properties.getBucket(), objectKey, expire, HttpMethod.GET);
            return url.toString();
        } catch (Exception e) {
            throw new BizException(ErrorCode.OSS_ERROR, "上传失败：" + e.getMessage());
        }
    }
}
