package com.pethome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.ReviewCreateDTO;
import com.pethome.entity.Review;
import com.pethome.mapper.ReviewMapper;
import com.pethome.security.UserContext;
import com.pethome.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评价服务实现
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    @Override
    public Review create(ReviewCreateDTO dto) {
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BizException(ErrorCode.PARAM_ERROR, "评分需在 1-5 之间");
        }
        Review review = new Review();
        review.setOrderType(dto.getOrderType());
        review.setOrderId(dto.getOrderId());
        review.setOwnerId(UserContext.getUserId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        reviewMapper.insert(review);
        return review;
    }

    @Override
    public List<Review> listByOrder(String orderType, Long orderId) {
        return reviewMapper.selectList(new LambdaQueryWrapper<Review>()
                .eq(Review::getOrderType, orderType)
                .eq(Review::getOrderId, orderId)
                .orderByDesc(Review::getId));
    }
}
