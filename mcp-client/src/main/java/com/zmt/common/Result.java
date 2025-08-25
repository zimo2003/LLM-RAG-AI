package com.zmt.common;

import com.zmt.enums.ResultCode;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Result<T> {
    private ResultCode code;

    private String message;

    private T data;

    private Result(ResultCode code) {
        super();
        this.code = code;
    }

    private Result(ResultCode code, T data) {
        super();
        this.code = code;
        this.data = data;
    }

    private Result(ResultCode code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    /**
     * 数据是否接受成功
     *
     * @return 如果是，返回<code>true</code>，否则返回 <code>false</code>
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.equals(code);
    }

    /**
     * 请求成功！
     */
    public static Result ok() {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 请求成功！
     *
     * @param data 返回前端数据
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    /**
     * 请求失败！
     */
    public static Result fail() {
        return new Result(ResultCode.UNKNOWN_ERROR);
    }

    /**
     * 请求失败！
     *
     * @param errorCode 错误码
     */
    public static Result fail(ResultCode errorCode) {
        return new Result(errorCode, errorCode.getMessage());
    }

    /**
     * 请求失败！
     *
     * @param errorMessage 错误信息
     */
    public static Result fail(String errorMessage) {
        return fail(ResultCode.UNKNOWN_ERROR, errorMessage);
    }

    /**
     * 请求失败！
     *
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     */
    public static Result fail(ResultCode errorCode, String errorMessage) {
        return new Result(errorCode, errorMessage);
    }


    public Integer getCode() {
        return this.code.getCode();
    }

    public ResultCode getResultCode() {
        return this.code;
    }

}
