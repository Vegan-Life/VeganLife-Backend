package com.konggogi.veganlife.notification.domain;

public enum NotificationMessage implements MessageFormatter {
    // sse
    SSE_CONNECTION("SSE 연결이 완료되었습니다."),

    // intake
    OVER_INTAKE("오늘의 칼로리 섭취량이 %dKcal 초과되었습니다.") {
        @Override
        public String getMessage(int calorie) {
            return String.format(this.getMessage(), calorie);
        }
    },

    // comment
    ADD_COMMENT("%s님이 %s님의 게시글에 댓글을 달았습니다!") {
        @Override
        public String getMessage(String commentAuthor, String postAuthor) {
            return String.format(this.getMessage(), commentAuthor, postAuthor);
        }
    };

    private final String message;

    NotificationMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
