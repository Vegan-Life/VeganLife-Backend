package com.konggogi.veganlife.member.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum Gender {
    M,
    F;

    @JsonCreator
    public static Gender parsing(String input) {
        return Stream.of(Gender.values())
                .filter(gender -> gender.name().equals(input.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
