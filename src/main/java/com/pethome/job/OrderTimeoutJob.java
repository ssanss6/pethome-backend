package com.pethome.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务占位：未支付订单超时取消
 */
@Slf4j
@Component
public class OrderTimeoutJob {

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void cancelTimeoutOrders() {
        // TODO M6：扫描待支付且超时的寄养/洗护订单，置为已取消；注意幂等与批量
    }
}
