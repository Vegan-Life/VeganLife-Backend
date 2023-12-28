package com.konggogi.veganlife.sse.domain;

public interface MessageFormatter {
    default String getMessage() {
        return "";
    }

    default String getMessage(int data) {
        return getMessage();
    }
}
