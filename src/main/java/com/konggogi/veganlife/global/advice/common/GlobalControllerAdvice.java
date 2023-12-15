package com.konggogi.veganlife.global.advice.common;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.exception.dto.response.ErrorResponse;
import com.konggogi.veganlife.global.util.AopUtils;
import com.konggogi.veganlife.global.util.LoggingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            HandlerMethod handlerMethod, MethodArgumentNotValidException exception) {

        String message = LoggingUtils.methodArgumentNotValidMessage(exception);
        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod),
                HttpStatus.BAD_REQUEST,
                exception,
                message);

        ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE, message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            HandlerMethod handlerMethod, MethodArgumentTypeMismatchException exception) {

        String message = LoggingUtils.methodArgumentTypeMismatchMessage(exception);
        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod),
                HttpStatus.BAD_REQUEST,
                exception,
                message);

        ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE, message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameterException(
            HandlerMethod handlerMethod, MissingServletRequestParameterException exception) {

        String message = LoggingUtils.missingRequestParameterMessage(exception);
        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod),
                HttpStatus.BAD_REQUEST,
                exception,
                message);

        ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE, message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NotFoundEntityException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            HandlerMethod handlerMethod, NotFoundEntityException exception) {

        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod), HttpStatus.NOT_FOUND, exception);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.from(exception.getErrorCode()));
    }
}
