package com.pethome.service.storage;

public interface OssService {

    /** 获取对象访问签名 URL（GET） */
    String generateAccessUrl(String objectKey);

    /** 获取对象直传签名 URL（PUT） */
    String generateUploadUrl(String objectKey);

    /** 连通性自检：真实上传一个测试对象并读回后删除 */
    String selfTest();
}
