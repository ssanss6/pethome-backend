package com.pethome.service;

import java.math.BigDecimal;

/**
 * 支付网关抽象（M6）：真实支付（支付宝沙箱/微信）与模拟支付共用此接口
 */
public interface PaymentService {

    /** 发起支付（模拟实现：直接成功） */
    void pay(String orderType, Long orderId, BigDecimal amount);

    /** 退款（模拟实现：直接成功） */
    void refund(String orderType, Long orderId, BigDecimal amount);
}
