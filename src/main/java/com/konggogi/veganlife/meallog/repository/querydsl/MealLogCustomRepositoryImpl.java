package com.konggogi.veganlife.meallog.repository.querydsl;

import static com.konggogi.veganlife.meallog.domain.QMeal.meal;
import static com.konggogi.veganlife.meallog.domain.QMealLog.mealLog;

import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.member.service.dto.TotalCalorieOfMealType;
import com.querydsl.core.types.Projections;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class MealLogCustomRepositoryImpl extends QuerydslRepositorySupport
        implements MealLogCustomRepository {
    public MealLogCustomRepositoryImpl() {
        super(MealLog.class);
    }

    @Override
    public List<TotalCalorieOfMealType> sumCaloriesOfMealTypeByMemberIdAndCreatedAtBetween(
            Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return from(mealLog)
                .join(mealLog.meals, meal)
                .where(
                        mealLog.member
                                .id
                                .eq(memberId)
                                .and(mealLog.createdAt.between(startDateTime, endDateTime)))
                .groupBy(mealLog.mealType)
                .select(
                        Projections.fields(
                                TotalCalorieOfMealType.class,
                                mealLog.mealType,
                                meal.calorie.sum().as("totalCalorie")))
                .fetch();
    }
}
