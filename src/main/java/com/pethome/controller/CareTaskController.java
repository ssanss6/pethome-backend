package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.CareTaskCompleteDTO;
import com.pethome.security.UserContext;
import com.pethome.service.CareTaskService;
import com.pethome.vo.CareTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 照护任务接口
 */
@Tag(name = "照护任务")
@RestController
@RequestMapping("/api/care")
@RequiredArgsConstructor
public class CareTaskController {

    private final CareTaskService careTaskService;

    @Operation(summary = "某日照护任务列表（员工）")
    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<List<CareTaskVO>> list(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.ok(careTaskService.listByDate(date));
    }

    @Operation(summary = "照护任务打卡")
    @PostMapping("/tasks/{id}/complete")
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> complete(@PathVariable Long id, @RequestBody(required = false) CareTaskCompleteDTO dto) {
        careTaskService.complete(id, dto == null ? new CareTaskCompleteDTO() : dto);
        return Result.ok();
    }

    @Operation(summary = "订单照护动态（宠物主）")
    @GetMapping("/orders/{orderId}/tasks")
    public Result<List<CareTaskVO>> orderTasks(@PathVariable Long orderId) {
        return Result.ok(careTaskService.listByOrder(orderId, UserContext.getUserId()));
    }
}
