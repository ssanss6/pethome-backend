package com.pethome.enums;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * 洗护订单状态 + 流转白名单
 */
@Getter
public enum GroomingOrderStatus {

    PENDING_PAY("PENDING_PAY", "待支付"),
    BOOKED("BOOKED", "已预约"),
    IN_SERVICE("IN_SERVICE", "服务中"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String desc;

    private static final Map<GroomingOrderStatus, Set<GroomingOrderStatus>> TRANSITIONS = Map.of(
            PENDING_PAY, Set.of(BOOKED, CANCELLED),
            BOOKED, Set.of(IN_SERVICE, CANCELLED),
            IN_SERVICE, Set.of(COMPLETED)
    );

    GroomingOrderStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static GroomingOrderStatus fromCode(String code) {
        for (GroomingOrderStatus s : values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知洗护订单状态: " + code);
    }

    public boolean canTransitionTo(GroomingOrderStatus target) {
        Set<GroomingOrderStatus> allowed = TRANSITIONS.get(this);
        return allowed != null && allowed.contains(target);
    }
}
