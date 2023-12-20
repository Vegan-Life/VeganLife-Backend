package com.konggogi.veganlife.meallog.fixture;


import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.meallog.domain.Meal;

public enum MealFixture {
    DEFAULT("디폴트 음식", 100, IntakeUnit.G, 100, 10, 10, 10);

    private String name;
    private Integer intake;
    private IntakeUnit intakeUnit;
    private Integer calorie;
    private Integer carbs;
    private Integer protein;
    private Integer fat;

    MealFixture(
            String name,
            Integer intake,
            IntakeUnit intakeUnit,
            Integer calorie,
            Integer carbs,
            Integer protein,
            Integer fat) {
        this.name = name;
        this.intake = intake;
        this.intakeUnit = intakeUnit;
        this.calorie = calorie;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public Meal get(MealData mealData) {

        return Meal.builder()
                .name(name)
                .intake(intake)
                .intakeUnit(intakeUnit)
                .calorie(calorie)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .mealData(mealData)
                .build();
    }

    public Meal getWithId(Long id, MealData mealData) {

        return Meal.builder()
                .id(id)
                .name(name)
                .intake(intake)
                .intakeUnit(intakeUnit)
                .calorie(calorie)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .mealData(mealData)
                .build();
    }
}
