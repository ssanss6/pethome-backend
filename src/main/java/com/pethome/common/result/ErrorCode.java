package com.pethome.common.result;

import lombok.Getter;

/**
 * 全局错误码
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "success"),
    UNAUTHORIZED(401, "未认证或登录已失效"),
    FORBIDDEN(403, "无权限访问"),

    PARAM_ERROR(40001, "参数校验失败"),
    CAGE_CONFLICT(40002, "笼位日期区间冲突"),
    SLOT_FULL(40003, "洗护时段已满"),
    ORDER_STATUS_ERROR(40004, "订单状态不允许该操作"),
    DUPLICATE_SUBMIT(40005, "重复提交"),
    BALANCE_NOT_ENOUGH(40006, "余额/次数不足"),
    USER_NOT_FOUND(40007, "用户不存在"),
    PASSWORD_ERROR(40008, "账号或密码错误"),
    USER_DISABLED(40009, "账号已被禁用"),
    WX_LOGIN_FAILED(40010, "微信登录失败"),
    OSS_ERROR(40011, "文件服务异常"),

    SYSTEM_ERROR(50000, "系统异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
