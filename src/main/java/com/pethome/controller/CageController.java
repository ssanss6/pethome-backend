package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.CageDTO;
import com.pethome.entity.Cage;
import com.pethome.service.CageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 笼位管理接口（后台）
 */
@RestController
@RequestMapping("/api/admin/cages")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
public class CageController {

    private final CageService cageService;

    @GetMapping
    public Result<List<Cage>> list(@RequestParam(required = false) Long roomTypeId) {
        return Result.ok(cageService.list(roomTypeId));
    }

    @GetMapping("/{id}")
    public Result<Cage> get(@PathVariable Long id) {
        return Result.ok(cageService.get(id));
    }

    @PostMapping
    public Result<Cage> create(@Valid @RequestBody CageDTO dto) {
        return Result.ok(cageService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Cage> update(@PathVariable Long id, @Valid @RequestBody CageDTO dto) {
        return Result.ok(cageService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cageService.delete(id);
        return Result.ok();
    }
}
