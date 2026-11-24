package com.pethome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.entity.BoardingOrder;
import com.pethome.entity.GroomingOrder;
import com.pethome.enums.BoardingOrderStatus;
import com.pethome.enums.GroomingOrderStatus;
import com.pethome.mapper.BoardingOrderMapper;
import com.pethome.mapper.CageMapper;
import com.pethome.mapper.GroomingOrderMapper;
import com.pethome.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表统计实现
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BoardingOrderMapper boardingOrderMapper;
    private final GroomingOrderMapper groomingOrderMapper;
    private final CageMapper cageMapper;

    @Override
    public Map<String, Object> summary() {
        long inBoarding = boardingOrderMapper.selectCount(new LambdaQueryWrapper<BoardingOrder>()
                .eq(BoardingOrder::getStatus, BoardingOrderStatus.BOARDING.getCode()));
        long totalCages = cageMapper.selectCount(null);
        long todayGroomingDone = groomingOrderMapper.selectCount(new LambdaQueryWrapper<GroomingOrder>()
                .eq(GroomingOrder::getStatus, GroomingOrderStatus.COMPLETED.getCode()));

        // 客单价：已完成寄养订单平均基础费
        List<BoardingOrder> completed = boardingOrderMapper.selectList(new LambdaQueryWrapper<BoardingOrder>()
                .eq(BoardingOrder::getStatus, BoardingOrderStatus.COMPLETED.getCode()));
        BigDecimal avg = BigDecimal.ZERO;
        for (BoardingOrder o : completed) {
            avg = avg.add(o.getBaseAmount() == null ? BigDecimal.ZERO : o.getBaseAmount());
        }
        if (!completed.isEmpty()) {
            avg = avg.divide(BigDecimal.valueOf(completed.size()), 2, RoundingMode.HALF_UP);
        }

        double rate = totalCages == 0 ? 0 : Math.round(inBoarding * 10000.0 / totalCages) / 100.0;

        Map<String, Object> map = new HashMap<>();
        map.put("inBoarding", inBoarding);
        map.put("totalCages", totalCages);
        map.put("occupancyRate", rate);
        map.put("todayGroomingDone", todayGroomingDone);
        map.put("avgOrderAmount", avg);
        return map;
    }
}
