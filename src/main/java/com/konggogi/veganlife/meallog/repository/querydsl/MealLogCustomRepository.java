package com.konggogi.veganlife.meallog.repository.querydsl;


import com.konggogi.veganlife.member.service.dto.TotalCalorieOfMealType;
import java.time.LocalDate;
import java.util.List;

public interface MealLogCustomRepository {

    List<TotalCalorieOfMealType> sumCaloriesOfMealTypeByMemberIdAndDateBetween(
            Long memberId, LocalDate startDate, LocalDate endDate);

    List<TotalCalorieOfMealType> sumCaloriesOfMealTypeByMemberIdAndDate(
            Long memberId, LocalDate date);
}
