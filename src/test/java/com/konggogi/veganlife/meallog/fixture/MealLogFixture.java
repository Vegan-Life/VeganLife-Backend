package com.konggogi.veganlife.meallog.fixture;


import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.domain.Member;
import java.util.List;

public enum MealLogFixture {
    BREAKFAST(MealType.BREAKFAST),
    BREAKFAST_SNACK(MealType.BREAKFAST_SNACK),
    LUNCH(MealType.LUNCH),
    LUNCH_SNACK(MealType.LUNCH_SNACK),
    DINNER(MealType.DINNER),
    DINNER_SNACK(MealType.DINNER_SNACK);

    private MealType mealType;

    MealLogFixture(MealType mealType) {
        this.mealType = mealType;
    }

    public MealLog get(List<Meal> meals, Member member) {

        MealLog mealLog = MealLog.builder().mealType(mealType).member(member).build();
        meals.forEach(mealLog::addMeal);
        return mealLog;
    }

    public MealLog getWithId(Long id, List<Meal> meals, Member member) {

        MealLog mealLog = MealLog.builder().id(id).mealType(mealType).member(member).build();
        meals.forEach(mealLog::addMeal);
        return mealLog;
    }
}
