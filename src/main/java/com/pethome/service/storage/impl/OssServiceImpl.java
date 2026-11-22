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
 * 阿里云 OSS 服务实现：服务端签名 URL + 直传，不暴露 AccessKey
 */
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final OSS ossClient;
    private final OssProperties properties;

    /**
     * 获取对象访问签名 URL（GET）
     *
     * @param objectKey 对象 key
     * @return 带过期时间的签名 URL
     */
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

    /**
     * 获取对象直传签名 URL（PUT）
     *
     * @param objectKey 对象 key
     * @return 带过期时间的签名 URL
     */
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

    /**
     * 连通性自检：真实上传一个测试对象并读回后删除
     *
     * @return 自检结果字符串
     */
    @Override
    public String selfTest() {
        String key = "_check/connectivity-" + System.currentTimeMillis() + ".txt";
        String content = "pethome-oss-ok";
        try {
            // 上传 -> 读回 -> 校验 -> 删除
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

    /**
     * 上传文件到 OSS，返回访问 URL（7 天有效签名）
     *
     * @param objectKey 对象 key
     * @param file      上传的文件
     * @return 访问签名 URL
     */
    @Override
    public String upload(String objectKey, MultipartFile file) {
        try {
            // 设置元信息后直传 OSS
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
