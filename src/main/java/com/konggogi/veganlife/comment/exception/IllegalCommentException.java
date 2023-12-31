package com.konggogi.veganlife.comment.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class IllegalCommentException extends ApiException {
    public IllegalCommentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public IllegalCommentException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
