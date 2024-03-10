package com.konggogi.veganlife.member.service.dto;


import com.konggogi.veganlife.meallog.domain.MealType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TotalCalorieOfMealType {
    private MealType mealType;
    private Integer totalCalorie;
}
