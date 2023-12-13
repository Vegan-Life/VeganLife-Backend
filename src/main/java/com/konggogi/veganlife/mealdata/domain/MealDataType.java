package com.konggogi.veganlife.mealdata.domain;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum MealDataType {
    MEAL("meal"),
    PROCESSED("processed");

    private String type;

    @JsonCreator
    MealDataType(String type) {
        this.type = type;
    }
}
