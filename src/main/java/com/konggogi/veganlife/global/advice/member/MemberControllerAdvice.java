package com.konggogi.veganlife.global.advice.member;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.dto.response.ErrorResponse;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.security.exception.InvalidOauthTokenException;
import com.konggogi.veganlife.global.security.exception.MismatchTokenException;
import com.konggogi.veganlife.global.util.AopUtils;
import com.konggogi.veganlife.global.util.LoggingUtils;
import com.konggogi.veganlife.member.exception.DuplicateNicknameException;
import com.konggogi.veganlife.member.exception.UnsupportedProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@RestControllerAdvice
public class MemberControllerAdvice {
    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedNicknameException(
            HandlerMethod handlerMethod, DuplicateNicknameException exception) {
        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod), HttpStatus.CONFLICT, exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.from(exception.getErrorCode()));
    }

    @ExceptionHandler({MismatchTokenException.class, UnsupportedProviderException.class})
    public ResponseEntity<ErrorResponse> handleMismatchTokenException(
            HandlerMethod handlerMethod, ApiException exception) {
        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod), HttpStatus.BAD_REQUEST, exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(exception.getErrorCode()));
    }

    @ExceptionHandler({InvalidJwtException.class, InvalidOauthTokenException.class})
    public ResponseEntity<ErrorResponse> handleInvalidJwtException(
            HandlerMethod handlerMethod, ApiException exception) {
        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod), HttpStatus.UNAUTHORIZED, exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.from(exception.getErrorCode()));
    }
}
