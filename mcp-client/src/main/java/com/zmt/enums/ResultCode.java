package com.zmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 参数错误
     */
    INVALID_ARGUMENT(-1, "参数错误"),

    /**
     * 鉴权失败
     */
    INVALID_AUTHORITY(-2, "无权限"),

    /**
     * 服务器错误
     */
    SERVER_ERROR(-3, "系统异常"),

    /**
     * 未登录
     */
    NO_LOGIN(-4, "未登录"),

    /**
     * 限制操作
     */
    SENTINEL(-5, "操作频繁"),

    /**
     * 图形验证码校验失败
     */
    CAPTCHA_ERROR(-6, "图形验证码校验失败"),

    /**
     * 微信未授权注册
     */
    WX_UN_AUTHORIZE(-7, "未授权注册"),

    /**
     * 登录失败
     */
    LOGIN_ERROR(-8, "登录失败"),

    /**
     * token已过期
     */
    TOKEN_EXPIRE(-9, "token已过期"),

    /**
     * 未知错误
     */
    UNKNOWN_ERROR(-999, "未知错误");


    private final Integer code;

    private final String message;

}