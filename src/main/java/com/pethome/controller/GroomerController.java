package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.GroomerDTO;
import com.pethome.entity.Groomer;
import com.pethome.service.GroomerService;
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
 * 美容师接口
 */
@Tag(name = "美容师")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroomerController {

    private final GroomerService groomerService;

    @Operation(summary = "美容师列表（预约用）")
    @GetMapping("/grooming/groomers")
    public Result<List<Groomer>> list() {
        return Result.ok(groomerService.list());
    }

    @Operation(summary = "新增美容师")
    @PostMapping("/admin/groomers")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Groomer> create(@RequestBody GroomerDTO dto) {
        return Result.ok(groomerService.create(dto));
    }

    @Operation(summary = "编辑美容师")
    @PutMapping("/admin/groomers/{id}")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Groomer> update(@PathVariable Long id, @RequestBody GroomerDTO dto) {
        return Result.ok(groomerService.update(id, dto));
    }

    @Operation(summary = "删除美容师")
    @DeleteMapping("/admin/groomers/{id}")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        groomerService.delete(id);
        return Result.ok();
    }
}
