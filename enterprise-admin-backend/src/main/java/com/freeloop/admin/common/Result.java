package com.freeloop.admin.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "统一接口响应")
public class Result<T> {
    @Schema(
            description = "业务响应码，0 表示成功，非 0 表示失败",
            example = "0"
    )
    private final int code;
    @Schema(
            description = "响应消息",
            example = "操作成功"
    )
    private final String message;
    @Schema(
            description = "实际响应数据；无返回数据或请求失败时为 null"
    )
    private final T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                data
        );
    }

    public static Result<Void> success() {
        return new Result<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                null
        );
    }

    public static <T> Result<T> failure(ResultCode resultCode) {
        return new Result<>(
                resultCode.getCode(),
                resultCode.getMessage(),
                null
        );
    }

    public static <T> Result<T> failure(
            ResultCode resultCode,
            String message) {
        return new Result<>(
                resultCode.getCode(),
                message,
                null
        );
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}