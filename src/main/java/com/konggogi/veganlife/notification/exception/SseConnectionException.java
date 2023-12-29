package com.konggogi.veganlife.notification.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class SseConnectionException extends ApiException {
    public SseConnectionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SseConnectionException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
