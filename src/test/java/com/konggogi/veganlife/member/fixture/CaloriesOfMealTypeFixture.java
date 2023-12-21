package com.konggogi.veganlife.member.fixture;


import com.konggogi.veganlife.member.service.dto.CaloriesOfMealType;

public enum CaloriesOfMealTypeFixture {
    DEFAULT(10, 10, 10, 10);
    private final int breakfast;
    private final int lunch;
    private final int dinner;
    private final int snack;

    CaloriesOfMealTypeFixture(int breakfast, int lunch, int dinner, int snack) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.snack = snack;
    }

    public CaloriesOfMealType get() {
        return new CaloriesOfMealType(breakfast, lunch, dinner, snack);
    }
}
