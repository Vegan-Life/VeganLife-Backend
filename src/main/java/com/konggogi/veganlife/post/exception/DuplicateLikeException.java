package com.konggogi.veganlife.post.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class DuplicateLikeException extends ApiException {
    public DuplicateLikeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateLikeException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
