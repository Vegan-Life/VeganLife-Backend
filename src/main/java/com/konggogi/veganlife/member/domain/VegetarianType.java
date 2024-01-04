package com.konggogi.veganlife.member.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum VegetarianType {
    VEGAN,
    LACTO,
    OVO,
    LACTO_OVO,
    PESCO;

    @JsonCreator
    public static VegetarianType parsing(String input) {
        return Stream.of(VegetarianType.values())
                .filter(type -> type.name().equals(input.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
