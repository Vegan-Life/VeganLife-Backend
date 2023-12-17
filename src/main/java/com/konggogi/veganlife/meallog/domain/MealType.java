package com.konggogi.veganlife.meallog.domain;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum MealType {
    BREAKFAST("아침 식사"),
    BREAKFAST_SNACK("아침 간식"),
    LUNCH("점심 식사"),
    LUNCH_SNACK("점심 간식"),
    DINNER("저녁 식사"),
    DINNER_SNACK("저녁 간식");

    private final String value;

    @JsonCreator
    MealType(String value) {
        this.value = value;
    }
}
