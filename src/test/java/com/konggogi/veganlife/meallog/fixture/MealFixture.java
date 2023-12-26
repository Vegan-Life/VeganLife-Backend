package com.konggogi.veganlife.meallog.fixture;


import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;

public enum MealFixture {
    DEFAULT(100, 100, 10, 10, 10);

    private Integer intake;
    private Integer calorie;
    private Integer carbs;
    private Integer protein;
    private Integer fat;

    MealFixture(Integer intake, Integer calorie, Integer carbs, Integer protein, Integer fat) {
        this.intake = intake;
        this.calorie = calorie;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public Meal get(MealData mealData) {

        return Meal.builder()
                .intake(intake)
                .calorie(calorie)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .mealData(mealData)
                .build();
    }

    public Meal get(Long id, MealData mealData) {

        return Meal.builder()
                .id(id)
                .intake(intake)
                .calorie(calorie)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .mealData(mealData)
                .build();
    }

    public Meal getWithMealLog(MealLog mealLog, MealData mealData) {

        return Meal.builder()
                .intake(intake)
                .calorie(calorie)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .mealLog(mealLog)
                .mealData(mealData)
                .build();
    }
}
