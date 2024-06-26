package com.konggogi.veganlife.global.exception;

public class EntityAccessDeniedException extends ApiException {

    public EntityAccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityAccessDeniedException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
