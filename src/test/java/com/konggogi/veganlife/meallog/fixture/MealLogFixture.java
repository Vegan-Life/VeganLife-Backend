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

    public MealLog getWithDate(List<Meal> meals, Member member, LocalDate date) {
        MealLog mealLog = MealLog.builder().mealType(mealType).member(member).build();
        meals.forEach(mealLog::addMeal);
        return setModifiedAt(mealLog, date);
    }

    private MealLog setModifiedAt(MealLog mealLog, LocalDate date) {
        Field modifiedAt = ReflectionUtils.findField(MealLog.class, "modifiedAt");
        ReflectionUtils.makeAccessible(modifiedAt);
        ReflectionUtils.setField(modifiedAt, mealLog, date.atStartOfDay());
        return mealLog;
    }
}
