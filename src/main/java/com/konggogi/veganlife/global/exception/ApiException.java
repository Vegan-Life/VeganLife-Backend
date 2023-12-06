package com.konggogi.veganlife.global.exception;


import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCode errorCode, String description) {
        super(description);
        this.errorCode = errorCode;
    }
}
