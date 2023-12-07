package com.konggogi.veganlife.test.controller.dto.response;

public record TestResponse(String message) {

    public static TestResponse from(String message) {

        return new TestResponse(message);
    }
}
