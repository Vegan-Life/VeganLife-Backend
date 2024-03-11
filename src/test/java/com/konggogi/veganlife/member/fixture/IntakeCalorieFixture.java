package com.konggogi.veganlife.member.fixture;


import com.konggogi.veganlife.member.service.dto.IntakeCalorie;

public enum IntakeCalorieFixture {
    DEFAULT(10, 10, 10, 10);
    private final int breakfast;
    private final int lunch;
    private final int dinner;
    private final int snack;

    IntakeCalorieFixture(int breakfast, int lunch, int dinner, int snack) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.snack = snack;
    }

    public IntakeCalorie get() {
        return new IntakeCalorie(breakfast, lunch, dinner, snack);
    }

    public IntakeCalorie getWithIntake(int intake) {
        return new IntakeCalorie(intake, intake, intake, intake);
    }
}
