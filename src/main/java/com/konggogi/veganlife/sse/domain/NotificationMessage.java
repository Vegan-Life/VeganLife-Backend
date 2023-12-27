package com.konggogi.veganlife.sse.domain;


import lombok.Getter;

@Getter
public enum NotificationMessage {
    // sse
    SSE_CONNECTION("SSE 연결이 완료되었습니다.");

    private final String message;

    NotificationMessage(String message) {
        this.message = message;
    }
}
