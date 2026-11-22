package com.pethome.controller;

import com.pethome.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "健康检查")
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Result<Map<String, String>> health() {
        return Result.ok(Map.of("status", "UP"));
    }
}
