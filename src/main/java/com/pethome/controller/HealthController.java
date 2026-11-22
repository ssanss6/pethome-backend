package com.pethome.controller;

import com.pethome.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查
 */
@Tag(name = "健康检查")
@RestController
public class HealthController {

    @Operation(summary = "健康检查")
    @GetMapping("/api/health")
    public Result<Map<String, String>> health() {
        return Result.ok(Map.of("status", "UP"));
    }
}
