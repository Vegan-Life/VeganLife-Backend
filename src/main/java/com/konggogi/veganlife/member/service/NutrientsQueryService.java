package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.service.dto.CaloriesOfMealType;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NutrientsQueryService {
    private final MemberQueryService memberQueryService;
    private final MealLogRepository mealLogRepository;

    public IntakeNutrients searchDailyIntakeNutrients(Long memberId, LocalDate date) {
        memberQueryService.search(memberId);
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(LocalTime.MAX);
        List<Meal> meals = findAllMealOfMealLog(memberId, startDateTime, endDateTime);
        return sumIntakeNutrients(meals);
    }

    public List<CaloriesOfMealType> searchWeeklyIntakeCalories(
            Long memberId, LocalDate startDate, LocalDate endDate) {
        memberQueryService.search(memberId);
        return startDate
                .datesUntil(endDate.plusDays(1))
                .map(
                        date -> {
                            LocalDateTime startDateTime = date.atStartOfDay();
                            LocalDateTime endDateTime = date.atTime(LocalTime.MAX);
                            List<MealLog> mealLogs =
                                    findMealLog(memberId, startDateTime, endDateTime);
                            return sumCalorieByMealType(mealLogs);
                        })
                .toList();
    }

    private List<Meal> findAllMealOfMealLog(
            Long memberId, LocalDateTime startDate, LocalDateTime endDate) {
        return findMealLog(memberId, startDate, endDate).stream()
                .map(MealLog::getMeals)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<MealLog> findMealLog(
            Long memberId, LocalDateTime startDate, LocalDateTime endDate) {
        return mealLogRepository.findAllByMemberIdAndModifiedAtBetween(
                memberId, startDate, endDate);
    }

    private IntakeNutrients sumIntakeNutrients(List<Meal> meals) {
        IntakeNutrients initIntakeNutrients = new IntakeNutrients(0, 0, 0, 0);
        return meals.stream()
                .reduce(
                        initIntakeNutrients,
                        (accumulator, meal) ->
                                new IntakeNutrients(
                                        accumulator.calorie() + meal.getCalorie(),
                                        accumulator.carbs() + meal.getCarbs(),
                                        accumulator.protein() + meal.getProtein(),
                                        accumulator.fat() + meal.getFat()),
                        IntakeNutrients::combine);
    }

    private CaloriesOfMealType sumCalorieByMealType(List<MealLog> mealLogs) {
        Map<MealType, Integer> caloriesByMealTypeGroup = calcCaloriesByMealTypeGroup(mealLogs);
        int snackTotalCalorie = sumSnackTotalCalorie(caloriesByMealTypeGroup);
        return new CaloriesOfMealType(
                caloriesByMealTypeGroup.getOrDefault(MealType.BREAKFAST, 0),
                caloriesByMealTypeGroup.getOrDefault(MealType.LUNCH, 0),
                caloriesByMealTypeGroup.getOrDefault(MealType.DINNER, 0),
                snackTotalCalorie);
    }

    private Map<MealType, Integer> calcCaloriesByMealTypeGroup(List<MealLog> mealLogs) {
        return mealLogs.stream()
                .flatMap(
                        mealLog ->
                                mealLog.getMeals().stream()
                                        .map(
                                                meal ->
                                                        new AbstractMap.SimpleEntry<>(
                                                                mealLog.getMealType(),
                                                                meal.getCalorie())))
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
    }

    private int sumSnackTotalCalorie(Map<MealType, Integer> caloriesByType) {
        return Arrays.stream(MealType.values())
                .filter(MealType::isSnack)
                .mapToInt(type -> caloriesByType.getOrDefault(type, 0))
                .sum();
    }
}
