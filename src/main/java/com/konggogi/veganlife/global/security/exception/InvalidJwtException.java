package com.konggogi.veganlife.global.security.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class InvalidJwtException extends ApiException {
    public InvalidJwtException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidJwtException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}