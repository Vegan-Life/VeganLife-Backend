package com.konggogi.veganlife.notification.domain;

public interface MessageFormatter {
    default String getMessage() {
        return "";
    }

    default String getMessage(int data) {
        return getMessage();
    }

    default String getMessage(String data, String subData) {
        return getMessage();
    }
}
