package com.konggogi.veganlife.global.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {
    // global
    UNHANDLED_ERROR("GLOBAL_999", "Handling 되지 않은 Server Error입니다."),

    // validation
    INVALID_INPUT_VALUE("GLOBAL_001", "RequestBody Validation에 실패했습니다."),

    // auth
    EXPIRED_TOKEN("AUTH_001", "JWT 토큰이 만료되었습니다."),
    INVALID_TOKEN("AUTH_002", "JWT 토큰이 유효하지 않습니다."),
    INVALID_TOKEN_SIGNATURE("AUTH_003", "JWT 서명이 유효하지 않습니다."),
    UNSUPPORTED_TOKEN("AUTH_004", "지원되지 않는 JWT 토큰입니다."),
    UNEXPECTED_TOKEN("AUTH_005", "JWT 처리 중 예상치 못한 오류가 발생했습니다."),
    NOT_FOUND_USER_INFO_TOKEN("AUTH_006", "JWT 사용자 정보를 가져올 수 없습니다."),
    NOT_FOUND_MATCH_REFRESH_TOKEN("AUTH_007", "일치하는 RefreshToken이 없습니다."),
    AUTHENTICATION_REQUIRED("AUTH_009", "인증이 필요한 URI입니다."),
    ACCESS_DENIED("AUTH_010", "Resource에 접근할 수 있는 권한이 없습니다."),

    // oauth
    UNSUPPORTED_PROVIDER("OAUTH_001", "지원되지 않는 provider입니다."),
    INVALID_OAUTH_TOKEN("OAUTH_002", "Oauth 액세스 토큰이 유효하지 않습니다."),

    // member
    DUPLICATED_NICKNAME("MEMBER_001", "중복된 닉네임입니다."),
    NOT_FOUND_MEMBER("MEMBER_002", "사용자를 찾을 수 없습니다."),

    // meal-data
    NOT_FOUND_MEAL_DATA("MEAL_DATA_001", "식품 정보를 찾을 수 없습니다."),

    // meal-log
    NOT_FOUND_MEAL_LOG("MEAL_LOG_001", "식사 기록을 찾을 수 없습니다."),

    // post
    NOT_FOUND_POST("POST_001", "게시글을 찾을 수 없습니다."),

    // post-like
    ALREADY_POST_LIKED("POST_LIKE_001", "좋아요가 되어 있는 게시글입니다."),
    ALREADY_POST_UNLIKED("POST_LIKE_002", "좋아요가 되어 있지 않은 게시글입니다."),

    // sse
    SSE_CONNECTION_ERROR("SSE_001", "SSE 연결이 실패했습니다. 재연결이 필요합니다."),
    NOT_FOUND_EMITTER("SSE_002", "SseEmitter를 찾을 수 없습니다."),

    // comment
    NOT_FOUND_COMMENT("COMMENT_001", "댓글을 찾을 수 없습니다."),
    IS_NOT_PARENT_COMMENT("COMMENT_002", "최상위 댓글에만 댓글을 등록할 수 있습니다."),

    // comment-like
    ALREADY_COMMENT_LIKED("COMMENT_LIKE_001", "좋아요가 되어 있는 댓글입니다."),
    ALREADY_COMMENT_UNLIKED("COMMENT_LIKE_002", "좋아요가 되어 있지 않은 댓글입니다."),

    // recipe
    NOT_FOUND_RECIPE("RECIPE_001", "레시피를 찾을 수 없습니다."),

    // recipe-like
    ALREADY_RECIPE_LIKED("RECIPE_LIKE_001", "좋아요가 되어 있는 레시피입니다."),
    ALREADY_RECIPE_UNLIKED("RECIPE_LIKE_002", "좋아요가 되어 있지 않은 레시피입니다.");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
