package com.konggogi.veganlife.global.security.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class MismatchTokenException extends ApiException {
    public MismatchTokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MismatchTokenException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
