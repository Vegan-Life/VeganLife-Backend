package com.konggogi.veganlife.post.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class IllegalLikeStatusException extends ApiException {
    public IllegalLikeStatusException(ErrorCode errorCode) {
        super(errorCode);
    }

    public IllegalLikeStatusException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
