package com.konggogi.veganlife.global.advice.post;


import com.konggogi.veganlife.comment.exception.IllegalCommentException;
import com.konggogi.veganlife.global.exception.dto.response.ErrorResponse;
import com.konggogi.veganlife.global.util.AopUtils;
import com.konggogi.veganlife.global.util.LoggingUtils;
import com.konggogi.veganlife.like.exception.IllegalLikeStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@RestControllerAdvice
public class PostControllerAdvice {
    @ExceptionHandler(IllegalLikeStatusException.class)
    public ResponseEntity<ErrorResponse> handleIllegalLikeStatusException(
            HandlerMethod handlerMethod, IllegalLikeStatusException exception) {
        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod), HttpStatus.CONFLICT, exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.from(exception.getErrorCode()));
    }

    @ExceptionHandler(IllegalCommentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalCommentException(
            HandlerMethod handlerMethod, IllegalCommentException exception) {
        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod), HttpStatus.BAD_REQUEST, exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(exception.getErrorCode()));
    }
}
