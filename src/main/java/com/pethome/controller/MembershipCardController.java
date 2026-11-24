package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.MembershipCardCreateDTO;
import com.pethome.entity.MembershipCard;
import com.pethome.service.MembershipCardService;
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
 * 会员次卡接口
 */
@Tag(name = "会员次卡")
@RestController
@RequestMapping("/api/membership/cards")
@RequiredArgsConstructor
public class MembershipCardController {

    private final MembershipCardService membershipCardService;

    @Operation(summary = "购买次卡")
    @PostMapping
    public Result<MembershipCard> create(@RequestBody MembershipCardCreateDTO dto) {
        return Result.ok(membershipCardService.create(dto));
    }

    @Operation(summary = "我的次卡")
    @GetMapping
    public Result<List<MembershipCard>> listMine() {
        return Result.ok(membershipCardService.listMine());
    }

    @Operation(summary = "次卡核销扣次")
    @PostMapping("/{id}/punch")
    @PreAuthorize("hasAnyRole('CAREGIVER','RECEPTION','STORE_MANAGER','PLATFORM_ADMIN')")
    public Result<Void> punch(@PathVariable Long id) {
        membershipCardService.punch(id);
        return Result.ok();
    }
}
