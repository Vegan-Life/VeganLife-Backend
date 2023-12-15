package com.konggogi.veganlife.global.advice.common;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.exception.dto.response.ErrorResponse;
import com.konggogi.veganlife.global.util.AopUtils;
import com.konggogi.veganlife.global.util.LoggingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            HandlerMethod handlerMethod, MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder errors = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.append("[");
            errors.append(fieldError.getField());
            errors.append("](은)는 ");
            errors.append(fieldError.getDefaultMessage());
            errors.append(" -> 입력된 값: ");
            errors.append(fieldError.getRejectedValue());
            errors.append(". ");
        }
        errors.deleteCharAt(errors.length() - 1);

        LoggingUtils.exceptionLog(
                AopUtils.extractMethodSignature(handlerMethod),
                HttpStatus.BAD_REQUEST,
                exception,
                errors.toString());

        ErrorResponse errorResponse =
                ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE, errors.toString());
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
