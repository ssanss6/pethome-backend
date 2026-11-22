package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.service.storage.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件接口（OSS 签名 URL）
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final OssService ossService;

    /** 获取对象访问签名 URL */
    @GetMapping("/sign-url")
    public Result<String> signUrl(@RequestParam String objectKey) {
        return Result.ok(ossService.generateAccessUrl(objectKey));
    }

    /** 获取对象直传签名 URL（PUT 上传） */
    @PostMapping("/upload-sign")
    public Result<String> uploadSign(@RequestParam String objectKey) {
        return Result.ok(ossService.generateUploadUrl(objectKey));
    }

    /** OSS 连通性自检：真实上传→读回→删除 */
    @GetMapping("/oss-check")
    public Result<String> ossCheck() {
        return Result.ok(ossService.selfTest());
    }
}
