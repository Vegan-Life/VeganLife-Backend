package com.konggogi.veganlife.global.advice.notification;


import com.konggogi.veganlife.global.exception.dto.response.ErrorResponse;
import com.konggogi.veganlife.global.util.AopUtils;
import com.konggogi.veganlife.global.util.LoggingUtils;
import com.konggogi.veganlife.notification.exception.SseConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@RestControllerAdvice
public class SseControllerAdvice {
    @ExceptionHandler(SseConnectionException.class)
    public ResponseEntity<ErrorResponse> handleSseConnectionException(
            HandlerMethod handlerMethod, SseConnectionException exception) {

        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod),
                HttpStatus.SERVICE_UNAVAILABLE,
                exception);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.from(exception.getErrorCode()));
    }
}
