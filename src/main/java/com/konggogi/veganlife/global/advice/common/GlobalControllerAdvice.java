package com.konggogi.veganlife.global.advice.common;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> signValidException(
            MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder errors = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.append("[");
            errors.append(fieldError.getField());
            errors.append("](은)는 ");
            errors.append(fieldError.getDefaultMessage());
            errors.append(". ");
        }
        errors.deleteCharAt(errors.length() - 1);

        ErrorResponse errorResponse =
                ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE, errors.toString());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
