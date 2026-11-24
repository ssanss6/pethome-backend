package com.pethome.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.entity.BoardingOrder;
import com.pethome.entity.GroomingOrder;
import com.pethome.enums.BoardingOrderStatus;
import com.pethome.enums.GroomingOrderStatus;
import com.pethome.mapper.BoardingOrderMapper;
import com.pethome.mapper.GroomingOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单超时取消定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutJob {

    private static final long TIMEOUT_MINUTES = 30;

    private final BoardingOrderMapper boardingOrderMapper;
    private final GroomingOrderMapper groomingOrderMapper;

    @Scheduled(fixedDelay = 60_000)
    public void cancelTimeoutOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);
        // 寄养订单超时未支付
        List<BoardingOrder> boarding = boardingOrderMapper.selectList(new LambdaQueryWrapper<BoardingOrder>()
                .eq(BoardingOrder::getStatus, BoardingOrderStatus.PENDING_PAY.getCode())
                .le(BoardingOrder::getCreatedAt, deadline));
        for (BoardingOrder o : boarding) {
            o.setStatus(BoardingOrderStatus.CANCELLED.getCode());
            boardingOrderMapper.updateById(o);
            log.info("[定时任务] 寄养订单超时取消 orderId={}", o.getId());
        }
        // 洗护订单超时未支付
        List<GroomingOrder> grooming = groomingOrderMapper.selectList(new LambdaQueryWrapper<GroomingOrder>()
                .eq(GroomingOrder::getStatus, GroomingOrderStatus.PENDING_PAY.getCode())
                .le(GroomingOrder::getCreatedAt, deadline));
        for (GroomingOrder o : grooming) {
            o.setStatus(GroomingOrderStatus.CANCELLED.getCode());
            groomingOrderMapper.updateById(o);
            log.info("[定时任务] 洗护订单超时取消 orderId={}", o.getId());
        }
    }
}
