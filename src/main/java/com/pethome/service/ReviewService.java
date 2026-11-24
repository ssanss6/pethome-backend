package com.pethome.service;

import com.pethome.dto.ReviewCreateDTO;
import com.pethome.entity.Review;

import java.util.List;

public interface ReviewService {

    Review create(ReviewCreateDTO dto);

    List<Review> listByOrder(String orderType, Long orderId);
}
