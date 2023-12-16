package com.konggogi.veganlife.mealdata.fixture;


import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.MealDataType;
import com.konggogi.veganlife.mealdata.domain.OwnerType;
import com.konggogi.veganlife.member.domain.Member;

public enum MealDataFixture {
    MEAL("디폴트 음식", MealDataType.MEAL, 100, 100, 10D, 1D, 1D, 1D, IntakeUnit.G, OwnerType.ALL),
    PROCESSED(
            "디폴트 가공식품",
            MealDataType.PROCESSED,
            100,
            100,
            10D,
            1D,
            1D,
            1D,
            IntakeUnit.G,
            OwnerType.MEMBER);

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
    private OwnerType ownerType;

    MealDataFixture(
            String name,
            MealDataType type,
            Integer amount,
            Integer amountPerServe,
            Double caloriePerUnit,
            Double proteinPerUnit,
            Double fatPerUnit,
            Double carbsPerUnit,
            IntakeUnit intakeUnit,
            OwnerType ownerType) {
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.amountPerServe = amountPerServe;
        this.caloriePerUnit = caloriePerUnit;
        this.proteinPerUnit = proteinPerUnit;
        this.fatPerUnit = fatPerUnit;
        this.carbsPerUnit = carbsPerUnit;
        this.intakeUnit = intakeUnit;
        this.ownerType = ownerType;
    }

    public MealData get(Member member) {

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
                intakeUnit,
                ownerType,
                member);
    }

    public MealData getWithName(String name, Member member) {

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
                intakeUnit,
                ownerType,
                member);
    }
}
