package com.konggogi.veganlife.global.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {
    // validation
    INVALID_INPUT_VALUE("GLOBAL_001", "RequestBody Validation에 실패했습니다."),

    // auth
    EXPIRED_TOKEN("AUTH_001", "JWT 토큰이 만료되었습니다."),
    INVALID_TOKEN("AUTH_002", "JWT 토큰이 유효하지 않습니다."),
    INVALID_TOKEN_SIGNATURE("AUTH_003", "JWT 서명이 유효하지 않습니다."),
    UNSUPPORTED_TOKEN("AUTH_004", "지원되지 않는 JWT 토큰입니다."),
    UNEXPECTED_TOKEN("AUTH_005", "JWT 처리 중 예상치 못한 오류가 발생했습니다."),
    NOT_FOUND_USER_INFO_TOKEN("AUTH_006", "JWT 사용자 정보를 가져올 수 없습니다."),
    NOT_FOUND_REFRESH_TOKEN("AUTH_007", "RefreshToken이 존재하지 않습니다."),
    MISMATCH_REFRESH_TOKEN("AUTH_008", "RefreshToken이 일치하지 않습니다."),

    // oauth
    UNSUPPORTED_PROVIDER("OATUH_001", "지원되지 않는 provider입니다."),

    // member
    DUPLICATED_NICKNAME("MEMBER_001", "중복된 닉네임입니다."),
    NOT_FOUND_MEMBER("MEMBER_002", "사용자를 찾을 수 없습니다.");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
