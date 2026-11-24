package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.ReviewCreateDTO;
import com.pethome.entity.Review;
import com.pethome.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 评价接口
 */
@Tag(name = "评价")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "提交评价")
    @PostMapping
    public Result<Review> create(@RequestBody ReviewCreateDTO dto) {
        return Result.ok(reviewService.create(dto));
    }

    @Operation(summary = "某订单评价列表")
    @GetMapping
    public Result<List<Review>> list(@RequestParam String orderType, @RequestParam Long orderId) {
        return Result.ok(reviewService.listByOrder(orderType, orderId));
    }
}
