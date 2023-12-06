package com.konggogi.veganlife.global.exception.dto.response;


import com.konggogi.veganlife.global.exception.ErrorCode;

public record ErrorResponse(String errorCode, String description) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getDescription());
    }

    public static ErrorResponse from(ErrorCode errorCode, String description) {
        return new ErrorResponse(errorCode.getCode(), description);
    }
}
