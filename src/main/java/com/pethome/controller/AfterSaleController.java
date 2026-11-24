package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.AfterSaleCreateDTO;
import com.pethome.entity.AfterSale;
import com.pethome.service.AfterSaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 售后工单接口
 */
@Tag(name = "售后工单")
@RestController
@RequestMapping("/api/after-sales")
@RequiredArgsConstructor
public class AfterSaleController {

    private final AfterSaleService afterSaleService;

    @Operation(summary = "提交售后")
    @PostMapping
    public Result<AfterSale> create(@RequestBody AfterSaleCreateDTO dto) {
        return Result.ok(afterSaleService.create(dto));
    }

    @Operation(summary = "我的售后")
    @GetMapping("/mine")
    public Result<List<AfterSale>> listMine() {
        return Result.ok(afterSaleService.listMine());
    }

    @Operation(summary = "全部售后（员工）")
    @GetMapping
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<List<AfterSale>> listAll() {
        return Result.ok(afterSaleService.listAll());
    }

    @Operation(summary = "处理售后")
    @PostMapping("/{id}/handle")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> handle(@PathVariable Long id, @RequestParam String status) {
        afterSaleService.handle(id, status);
        return Result.ok();
    }
}
