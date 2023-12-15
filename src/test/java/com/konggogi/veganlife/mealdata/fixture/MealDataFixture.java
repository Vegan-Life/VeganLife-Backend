package com.konggogi.veganlife.mealdata.fixture;


import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.MealDataType;

public enum MealDataFixture {
    MEAL("디폴트 음식", MealDataType.MEAL, 100, 100, 10D, 1D, 1D, 1D, IntakeUnit.G),
    PROCESSED("디폴트 가공식품", MealDataType.PROCESSED, 100, 100, 10D, 1D, 1D, 1D, IntakeUnit.G);

    private Long id = 1L;
    private String name;
    private MealDataType type;
    private Integer amount;
    private Integer amountPerServe;
    private Double caloriePerUnit;
    private Double proteinPerUnit;
    private Double fatPerUnit;
    private Double carbsPerUnit;
    private IntakeUnit intakeUnit;

    MealDataFixture(
            String name,
            MealDataType type,
            Integer amount,
            Integer amountPerServe,
            Double caloriePerUnit,
            Double proteinPerUnit,
            Double fatPerUnit,
            Double carbsPerUnit,
            IntakeUnit intakeUnit) {
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.amountPerServe = amountPerServe;
        this.caloriePerUnit = caloriePerUnit;
        this.proteinPerUnit = proteinPerUnit;
        this.fatPerUnit = fatPerUnit;
        this.carbsPerUnit = carbsPerUnit;
        this.intakeUnit = intakeUnit;
    }

    public MealData get() {

        return new MealData(
                id++,
                name,
                type,
                amount,
                amountPerServe,
                caloriePerUnit,
                proteinPerUnit,
                fatPerUnit,
                carbsPerUnit,
                intakeUnit);
    }

    public MealData getWithName(String name) {

        return new MealData(
                id++,
                name,
                type,
                amount,
                amountPerServe,
                caloriePerUnit,
                proteinPerUnit,
                fatPerUnit,
                carbsPerUnit,
                intakeUnit);
    }
}
