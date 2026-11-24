package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.BoardingOrderCreateDTO;
import com.pethome.dto.CheckInDTO;
import com.pethome.dto.CheckoutDTO;
import com.pethome.entity.BoardingOrder;
import com.pethome.entity.Cage;
import com.pethome.entity.Settlement;
import com.pethome.entity.RoomType;
import com.pethome.service.BoardingOrderService;
import com.pethome.service.RoomTypeService;
import com.pethome.vo.BoardingOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * 寄养预约接口
 */
@Tag(name = "寄养预约")
@RestController
@RequestMapping("/api/boarding")
@RequiredArgsConstructor
public class BoardingOrderController {

    private final BoardingOrderService boardingOrderService;
    private final RoomTypeService roomTypeService;

    @Operation(summary = "可订房型列表")
    @GetMapping("/room-types")
    public Result<List<RoomType>> roomTypes() {
        return Result.ok(roomTypeService.list());
    }

    @Operation(summary = "查询可订笼位")
    @GetMapping("/availability")
    public Result<List<Cage>> availability(
            @RequestParam Long roomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        return Result.ok(boardingOrderService.listAvailableCages(roomTypeId, checkInDate, checkOutDate));
    }

    @Operation(summary = "创建寄养订单")
    @PostMapping("/orders")
    public Result<BoardingOrder> create(@Valid @RequestBody BoardingOrderCreateDTO dto) {
        return Result.ok(boardingOrderService.create(dto));
    }

    @Operation(summary = "我的寄养订单列表")
    @GetMapping("/orders")
    public Result<List<BoardingOrder>> list() {
        return Result.ok(boardingOrderService.listMine());
    }

    @Operation(summary = "寄养订单详情")
    @GetMapping("/orders/{id}")
    public Result<BoardingOrderVO> detail(@PathVariable Long id) {
        return Result.ok(boardingOrderService.getMine(id));
    }

    @Operation(summary = "模拟支付")
    @PostMapping("/orders/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        boardingOrderService.pay(id);
        return Result.ok();
    }

    @Operation(summary = "取消订单")
    @PostMapping("/orders/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        boardingOrderService.cancel(id);
        return Result.ok();
    }

    @Operation(summary = "门店确认锁笼位")
    @PostMapping("/orders/{id}/confirm")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> confirm(@PathVariable Long id) {
        boardingOrderService.confirm(id);
        return Result.ok();
    }

    @Operation(summary = "入住登记")
    @PostMapping("/orders/{id}/check-in")
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> checkIn(@PathVariable Long id, @RequestBody(required = false) CheckInDTO dto) {
        boardingOrderService.checkIn(id, dto == null ? new CheckInDTO() : dto);
        return Result.ok();
    }

    @Operation(summary = "离店结算")
    @PostMapping("/orders/{id}/checkout")
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Settlement> checkout(@PathVariable Long id, @RequestBody(required = false) CheckoutDTO dto) {
        return Result.ok(boardingOrderService.checkout(id, dto == null ? new CheckoutDTO() : dto));
    }

    @Operation(summary = "离店确认")
    @PostMapping("/orders/{id}/complete")
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> complete(@PathVariable Long id) {
        boardingOrderService.complete(id);
        return Result.ok();
    }
}
