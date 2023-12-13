package com.konggogi.veganlife.mealdata.domain;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum IntakeUnit {
    G("g"),
    ML("ml");

    private String unit;

    @JsonCreator
    IntakeUnit(String unit) {
        this.unit = unit;
    }
}
