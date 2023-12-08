package com.konggogi.veganlife.member.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class UnsupportedProviderException extends ApiException {
    public UnsupportedProviderException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnsupportedProviderException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
