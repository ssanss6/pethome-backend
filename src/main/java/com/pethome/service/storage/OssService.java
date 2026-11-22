package com.pethome.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    /** 获取对象访问签名 URL（GET） */
    String generateAccessUrl(String objectKey);

    /** 获取对象直传签名 URL（PUT） */
    String generateUploadUrl(String objectKey);

    /** 连通性自检：真实上传一个测试对象并读回后删除 */
    String selfTest();

    /** 上传文件到 OSS，返回访问 URL（7 天有效签名） */
    String upload(String objectKey, MultipartFile file);
}
