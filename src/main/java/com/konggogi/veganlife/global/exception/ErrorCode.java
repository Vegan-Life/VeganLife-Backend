package com.konggogi.veganlife.global.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {
    // validation
    INVALID_INPUT_VALUE("GLOBAL_001", "RequestBody Validation에 실패했습니다.");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
