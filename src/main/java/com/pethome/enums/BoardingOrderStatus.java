package com.pethome.enums;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * 寄养订单状态 + 状态流转白名单
 */
@Getter
public enum BoardingOrderStatus {

    PENDING_PAY("PENDING_PAY", "待支付"),
    PENDING_CONFIRM("PENDING_CONFIRM", "待确认"),
    PENDING_CHECK_IN("PENDING_CHECK_IN", "待入住"),
    BOARDING("BOARDING", "寄养中"),
    PENDING_CHECK_OUT("PENDING_CHECK_OUT", "待离店"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消"),
    AFTER_SALE("AFTER_SALE", "售后中");

    private final String code;
    private final String desc;

    /** 状态流转白名单：from -> 允许的 to */
    private static final Map<BoardingOrderStatus, Set<BoardingOrderStatus>> TRANSITIONS = Map.of(
            PENDING_PAY, Set.of(PENDING_CONFIRM, CANCELLED),
            PENDING_CONFIRM, Set.of(PENDING_CHECK_IN, CANCELLED, AFTER_SALE),
            PENDING_CHECK_IN, Set.of(BOARDING, CANCELLED, AFTER_SALE),
            BOARDING, Set.of(PENDING_CHECK_OUT, AFTER_SALE),
            PENDING_CHECK_OUT, Set.of(COMPLETED),
            AFTER_SALE, Set.of(COMPLETED, CANCELLED)
    );

    BoardingOrderStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BoardingOrderStatus fromCode(String code) {
        for (BoardingOrderStatus s : values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知订单状态: " + code);
    }

    /** 判断当前状态(this)能否流转到目标状态(target) */
    public boolean canTransitionTo(BoardingOrderStatus target) {
        Set<BoardingOrderStatus> allowed = TRANSITIONS.get(this);
        return allowed != null && allowed.contains(target);
    }
}
