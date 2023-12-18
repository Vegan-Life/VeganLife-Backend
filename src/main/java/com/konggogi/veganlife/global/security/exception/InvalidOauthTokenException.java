package com.konggogi.veganlife.global.security.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class InvalidOauthTokenException extends ApiException {
    public InvalidOauthTokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidOauthTokenException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
