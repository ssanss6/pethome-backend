package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 经营报表接口
 */
@Tag(name = "经营报表")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "经营看板汇总")
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Map<String, Object>> summary() {
        return Result.ok(reportService.summary());
    }
}
