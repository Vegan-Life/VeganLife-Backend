package com.konggogi.veganlife.member.fixture;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;

public enum IntakeNutrientsFixture {
    DEFAULT(10, 10, 10, 10);
    private final int calorie;
    private final int carbs;
    private final int protein;
    private final int fat;

    IntakeNutrientsFixture(int calorie, int carbs, int protein, int fat) {
        this.calorie = calorie;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public IntakeNutrients get() {
        return new IntakeNutrients(calorie, carbs, protein, fat);
    }

    public IntakeNutrients getOverCalorieOfMember(Member member, float percentage) {
        int calorie = (int) (member.getAMR() * percentage);
        return new IntakeNutrients(calorie, carbs, protein, fat);
    }
}
