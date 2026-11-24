package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.ServiceItemDTO;
import com.pethome.entity.ServiceItem;
import com.pethome.service.ServiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 洗护服务项目接口
 */
@Tag(name = "洗护服务项目")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @Operation(summary = "服务项目列表（预约用）")
    @GetMapping("/grooming/services")
    public Result<List<ServiceItem>> list() {
        return Result.ok(serviceItemService.list());
    }

    @Operation(summary = "新增服务项目")
    @PostMapping("/admin/service-items")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<ServiceItem> create(@RequestBody ServiceItemDTO dto) {
        return Result.ok(serviceItemService.create(dto));
    }

    @Operation(summary = "编辑服务项目")
    @PutMapping("/admin/service-items/{id}")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<ServiceItem> update(@PathVariable Long id, @RequestBody ServiceItemDTO dto) {
        return Result.ok(serviceItemService.update(id, dto));
    }

    @Operation(summary = "删除服务项目")
    @DeleteMapping("/admin/service-items/{id}")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        serviceItemService.delete(id);
        return Result.ok();
    }
}
