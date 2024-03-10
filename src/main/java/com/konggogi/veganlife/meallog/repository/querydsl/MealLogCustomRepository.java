package com.konggogi.veganlife.meallog.repository.querydsl;


import com.konggogi.veganlife.member.service.dto.TotalCalorieOfMealType;
import java.time.LocalDateTime;
import java.util.List;

public interface MealLogCustomRepository {

    List<TotalCalorieOfMealType> sumCaloriesOfMealTypeByMemberIdAndCreatedAtBetween(
            Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
