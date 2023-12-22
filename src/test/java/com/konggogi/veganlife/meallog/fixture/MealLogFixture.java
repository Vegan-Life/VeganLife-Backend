package com.konggogi.veganlife.meallog.fixture;


import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.domain.Member;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.springframework.util.ReflectionUtils;

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

    public MealLog get(List<Meal> meals, List<MealImage> mealImages, Member member) {
        MealLog mealLog = MealLog.builder().mealType(mealType).member(member).build();
        meals.forEach(mealLog::addMeal);
        mealImages.forEach(mealLog::addMealImage);
        return mealLog;
    }

    public MealLog get(Long id, List<Meal> meals, List<MealImage> mealImages, Member member) {
        MealLog mealLog = MealLog.builder().id(id).mealType(mealType).member(member).build();
        meals.forEach(mealLog::addMeal);
        mealImages.forEach(mealLog::addMealImage);
        return mealLog;
    }

    public MealLog getWithDate(
            LocalDate date, List<Meal> meals, List<MealImage> mealImages, Member member) {
        MealLog mealLog = MealLog.builder().mealType(mealType).member(member).build();
        meals.forEach(mealLog::addMeal);
        mealImages.forEach(mealLog::addMealImage);
        return setCreatedAt(mealLog, date);
    }

    public MealLog getWithDate(
            Long id, LocalDate date, List<Meal> meals, List<MealImage> mealImages, Member member) {
        MealLog mealLog = MealLog.builder().id(id).mealType(mealType).member(member).build();
        meals.forEach(mealLog::addMeal);
        mealImages.forEach(mealLog::addMealImage);
        return setCreatedAt(mealLog, date);
    }

    private MealLog setCreatedAt(MealLog mealLog, LocalDate date) {
        Field createdAt = ReflectionUtils.findField(MealLog.class, "createdAt");
        ReflectionUtils.makeAccessible(createdAt);
        ReflectionUtils.setField(createdAt, mealLog, date.atStartOfDay());
        return mealLog;
    }
}
