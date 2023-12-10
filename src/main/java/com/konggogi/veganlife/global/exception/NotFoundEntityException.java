package com.konggogi.veganlife.global.exception;

public class NotFoundEntityException extends ApiException {
    public NotFoundEntityException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundEntityException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
