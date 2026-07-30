package com.freeloop.admin.exception;

import com.freeloop.admin.common.Result;
import com.freeloop.admin.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(
            BusinessException exception) {

        return buildErrorResponse(
                exception.getResultCode(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {

        ResultCode resultCode =
                ResultCode.PARAM_VALIDATION_FAILED;

        String message = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(resultCode.getMessage());

        return buildErrorResponse(resultCode, message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Result<Void>> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception) {

        ResultCode resultCode =
                ResultCode.PARAM_VALIDATION_FAILED;

        String message = exception
                .getParameterValidationResults()
                .stream()
                .flatMap(result ->
                        result.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(resultCode.getMessage());

        return buildErrorResponse(resultCode, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception) {

        return buildErrorResponse(
                ResultCode.REQUEST_BODY_INVALID
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {

        return buildErrorResponse(
                ResultCode.PARAMETER_TYPE_MISMATCH
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFoundException(
            NoResourceFoundException exception) {

        return buildErrorResponse(
                ResultCode.RESOURCE_NOT_FOUND
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception) {

        return buildErrorResponse(
                ResultCode.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(
            Exception exception) {

        LOGGER.error("发生未处理的系统异常", exception);

        return buildErrorResponse(
                ResultCode.INTERNAL_SERVER_ERROR
        );
    }

    private ResponseEntity<Result<Void>> buildErrorResponse(
            ResultCode resultCode) {

        return buildErrorResponse(
                resultCode,
                resultCode.getMessage()
        );
    }

    private ResponseEntity<Result<Void>> buildErrorResponse(
            ResultCode resultCode,
            String message) {

        Result<Void> result = Result.failure(
                resultCode,
                message
        );

        return ResponseEntity
                .status(resultCode.getHttpStatus())
                .body(result);
    }
}
