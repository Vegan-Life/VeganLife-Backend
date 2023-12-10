package com.konggogi.veganlife.member.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class DuplicateNicknameException extends ApiException {
    public DuplicateNicknameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
