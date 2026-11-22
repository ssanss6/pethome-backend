package com.pethome.controller;

import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.common.result.Result;
import com.pethome.security.UserContext;
import com.pethome.service.storage.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件接口（OSS 上传 / 签名 URL）
 */
@Tag(name = "文件/OSS")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final OssService ossService;

    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "文件不能为空");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new BizException(ErrorCode.PARAM_ERROR, "仅支持图片上传");
        }
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String objectKey = "upload/" + UserContext.getUserId() + "/"
                + UUID.randomUUID().toString().replace("-", "") + ext;
        String url = ossService.upload(objectKey, file);
        Map<String, String> data = new HashMap<>();
        data.put("objectKey", objectKey);
        data.put("url", url);
        return Result.ok(data);
    }

    @Operation(summary = "获取访问签名URL")
    @GetMapping("/sign-url")
    public Result<String> signUrl(@RequestParam String objectKey) {
        return Result.ok(ossService.generateAccessUrl(objectKey));
    }

    @Operation(summary = "获取上传签名URL")
    @PostMapping("/upload-sign")
    public Result<String> uploadSign(@RequestParam String objectKey) {
        return Result.ok(ossService.generateUploadUrl(objectKey));
    }

    @Operation(summary = "OSS连通性自检")
    @GetMapping("/oss-check")
    public Result<String> ossCheck() {
        return Result.ok(ossService.selfTest());
    }
}
