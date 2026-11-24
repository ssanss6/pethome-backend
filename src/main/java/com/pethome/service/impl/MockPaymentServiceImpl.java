package com.pethome.service.impl;

import com.pethome.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 模拟支付实现：不接入第三方，仅记录日志。真实支付替换此实现即可。
 */
@Slf4j
@Service
public class MockPaymentServiceImpl implements PaymentService {

    @Override
    public void pay(String orderType, Long orderId, BigDecimal amount) {
        log.info("[模拟支付] 支付成功 orderType={} orderId={} amount={}", orderType, orderId, amount);
    }

    @Override
    public void refund(String orderType, Long orderId, BigDecimal amount) {
        log.info("[模拟退款] 退款成功 orderType={} orderId={} amount={}", orderType, orderId, amount);
    }
}
