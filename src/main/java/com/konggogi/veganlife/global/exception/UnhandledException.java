package com.konggogi.veganlife.global.exception;

public class UnhandledException extends ApiException {

    public UnhandledException() {
        super(ErrorCode.UNHANDLED_ERROR);
    }

    public UnhandledException(String description) {
        super(ErrorCode.UNHANDLED_ERROR, description);
    }
}
