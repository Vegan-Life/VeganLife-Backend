package com.konggogi.veganlife.mealdata.domain;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum IntakeUnit {
    G("g"),
    ML("ml");

    private final String value;

    @JsonCreator
    IntakeUnit(String value) {
        this.value = value;
    }
}
