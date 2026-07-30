package com.freeloop.admin.common;

import org.springframework.http.HttpStatus;

public enum ResultCode {

    SUCCESS(0, HttpStatus.OK, "操作成功"),

    PARAM_VALIDATION_FAILED(
            40001,
            HttpStatus.BAD_REQUEST,
            "参数校验失败"
    ),

    REQUEST_BODY_INVALID(
            40002,
            HttpStatus.BAD_REQUEST,
            "请求体格式错误"
    ),

    PARAMETER_TYPE_MISMATCH(
            40003,
            HttpStatus.BAD_REQUEST,
            "参数类型错误"
    ),

    RESOURCE_NOT_FOUND(
            40400,
            HttpStatus.NOT_FOUND,
            "请求资源不存在"
    ),

    USER_NOT_FOUND(
            40401,
            HttpStatus.NOT_FOUND,
            "用户不存在"
    ),

    METHOD_NOT_ALLOWED(
            40500,
            HttpStatus.METHOD_NOT_ALLOWED,
            "请求方法不支持"
    ),

    USERNAME_ALREADY_EXISTS(
            40901,
            HttpStatus.CONFLICT,
            "用户名已存在"
    ),

    INTERNAL_SERVER_ERROR(
            50000,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "系统内部错误"
    );

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ResultCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}