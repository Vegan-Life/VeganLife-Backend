package com.konggogi.veganlife.mealdata.fixture;


import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.MealDataType;

public enum MealDataFixture {
    MEAL(
            "디폴트 음식",
            MealDataType.MEAL,
            100,
            100,
            100D,
            100D,
            100D,
            100D,
            100D,
            1D,
            1D,
            1D,
            IntakeUnit.G),
    PROCESSED(
            "디폴트 가공식품",
            MealDataType.PROCESSED,
            100,
            100,
            100D,
            100D,
            100D,
            100D,
            100D,
            1D,
            1D,
            1D,
            IntakeUnit.G);

    private Long id = 1L;
    private String name;
    private MealDataType type;
    private Integer amount;
    private Integer amountPerServe;
    private Double calorie;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double caloriePerGram;
    private Double proteinPerGram;
    private Double fatPerGram;
    private Double carbsPerGram;
    private IntakeUnit intakeUnit;

    MealDataFixture(
            String name,
            MealDataType type,
            Integer amount,
            Integer amountPerServe,
            Double calorie,
            Double protein,
            Double fat,
            Double carbs,
            Double caloriePerGram,
            Double proteinPerGram,
            Double fatPerGram,
            Double carbsPerGram,
            IntakeUnit intakeUnit) {
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.amountPerServe = amountPerServe;
        this.calorie = calorie;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.caloriePerGram = caloriePerGram;
        this.proteinPerGram = proteinPerGram;
        this.fatPerGram = fatPerGram;
        this.carbsPerGram = carbsPerGram;
        this.intakeUnit = intakeUnit;
    }

    public MealData get() {

        return new MealData(
                id++,
                name,
                type,
                amount,
                amountPerServe,
                calorie,
                protein,
                fat,
                carbs,
                caloriePerGram,
                proteinPerGram,
                fatPerGram,
                carbsPerGram,
                intakeUnit);
    }

    public MealData getWithName(String name) {

        return new MealData(
                id++,
                name,
                type,
                amount,
                amountPerServe,
                calorie,
                protein,
                fat,
                carbs,
                caloriePerGram,
                proteinPerGram,
                fatPerGram,
                carbsPerGram,
                intakeUnit);
    }

    public MealData getWithNameAndIntakeUnit(String name, IntakeUnit intakeUnit) {

        return new MealData(
                id++,
                name,
                type,
                amount,
                amountPerServe,
                calorie,
                protein,
                fat,
                carbs,
                caloriePerGram,
                proteinPerGram,
                fatPerGram,
                carbsPerGram,
                intakeUnit);
    }
}
