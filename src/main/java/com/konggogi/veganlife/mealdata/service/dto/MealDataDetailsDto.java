package com.konggogi.veganlife.mealdata.service.dto;


import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.MealDataType;
import com.konggogi.veganlife.mealdata.domain.PersonalMealData;
import lombok.Builder;

@Builder
public record MealDataDetailsDto(
        String name,
        MealDataType type,
        Integer amount,
        Integer amountPerServe,
        Double caloriePerUnit,
        Double proteinPerUnit,
        Double fatPerUnit,
        Double carbsPerUnit,
        IntakeUnit intakeUnit) {

    public static MealDataDetailsDto fromMealData(MealData mealData) {

        return MealDataDetailsDto.builder()
                .name(mealData.getName())
                .type(mealData.getType())
                .amount(mealData.getAmount())
                .amountPerServe(mealData.getAmountPerServe())
                .caloriePerUnit(mealData.getCaloriePerUnit())
                .proteinPerUnit(mealData.getProteinPerUnit())
                .fatPerUnit(mealData.getFatPerUnit())
                .carbsPerUnit(mealData.getCarbsPerUnit())
                .intakeUnit(mealData.getIntakeUnit())
                .build();
    }

    public static MealDataDetailsDto fromPersonalMealData(PersonalMealData mealData) {

        return MealDataDetailsDto.builder()
                .name(mealData.getName())
                .type(mealData.getType())
                .amount(mealData.getAmount())
                .amountPerServe(mealData.getAmountPerServe())
                .caloriePerUnit(mealData.getCaloriePerUnit())
                .proteinPerUnit(mealData.getProteinPerUnit())
                .fatPerUnit(mealData.getFatPerUnit())
                .carbsPerUnit(mealData.getCarbsPerUnit())
                .intakeUnit(mealData.getIntakeUnit())
                .build();
    }
}
