package com.konggogi.veganlife.member.exception;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;

public class DuplicatedNicknameException extends ApiException {
    public DuplicatedNicknameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
