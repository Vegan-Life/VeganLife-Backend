package com.konggogi.veganlife.notification.domain;

public interface MessageFormatter {
    default String getMessage() {
        return "";
    }

    default String getMessage(int data) {
        return getMessage();
    }
}
