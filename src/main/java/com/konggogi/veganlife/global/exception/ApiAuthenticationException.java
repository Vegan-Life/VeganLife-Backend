package com.konggogi.veganlife.global.exception;


import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class ApiAuthenticationException extends AuthenticationException {
    private final ErrorCode errorCode;

    public ApiAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public ApiAuthenticationException(ErrorCode errorCode, String description) {
        super(description);
        this.errorCode = errorCode;
    }
}
