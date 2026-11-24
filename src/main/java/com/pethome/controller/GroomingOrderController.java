package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.GroomingOrderCreateDTO;
import com.pethome.entity.GroomingOrder;
import com.pethome.service.GroomingOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 洗护订单接口
 */
@Tag(name = "洗护订单")
@RestController
@RequestMapping("/api/grooming/orders")
@RequiredArgsConstructor
public class GroomingOrderController {

    private final GroomingOrderService groomingOrderService;

    @Operation(summary = "创建洗护预约")
    @PostMapping
    public Result<GroomingOrder> create(@RequestBody GroomingOrderCreateDTO dto) {
        return Result.ok(groomingOrderService.create(dto));
    }

    @Operation(summary = "我的洗护订单")
    @GetMapping
    public Result<List<GroomingOrder>> listMine() {
        return Result.ok(groomingOrderService.listMine());
    }

    @Operation(summary = "全部洗护订单（员工）")
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<List<GroomingOrder>> listAll() {
        return Result.ok(groomingOrderService.listAll());
    }

    @Operation(summary = "洗护模拟支付")
    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        groomingOrderService.pay(id);
        return Result.ok();
    }

    @Operation(summary = "取消洗护订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        groomingOrderService.cancel(id);
        return Result.ok();
    }

    @Operation(summary = "到店签到")
    @PostMapping("/{id}/sign-in")
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> signIn(@PathVariable Long id) {
        groomingOrderService.signIn(id);
        return Result.ok();
    }

    @Operation(summary = "服务完成核销")
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> complete(@PathVariable Long id) {
        groomingOrderService.complete(id);
        return Result.ok();
    }
}
