package com.konggogi.veganlife.sse.domain;


import lombok.Getter;

@Getter
public enum NotificationMessage {
    // sse
    SSE_CONNECTION("SSE 연결이 완료되었습니다."),

    // intake
    OVER_INTAKE("오늘의 칼로리 섭취량이 %dKcal 초과되었습니다.");

    private final String message;

    NotificationMessage(String message) {
        this.message = message;
    }

    public String setCalorie(int calorie) {
        if (this == OVER_INTAKE) {
            return String.format(this.message, calorie);
        }
        return this.message;
    }
}
