package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.service.dto.CaloriesOfMealType;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.time.*;
import java.time.temporal.WeekFields;
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

    public List<CaloriesOfMealType> searchMonthlyIntakeCalories(
            Long memberId, LocalDate startDate) {
        memberQueryService.search(memberId);
        startDate = YearMonth.from(startDate).atDay(1);
        LocalDate endDate = YearMonth.from(startDate).atEndOfMonth();
        return getStartDatesOfWeeks(startDate, endDate).stream()
                .map(startDayOfWeek -> calcWeekCalories(memberId, startDayOfWeek))
                .toList();
    }

    public List<CaloriesOfMealType> searchYearlyIntakeCalories(Long memberId, LocalDate startDate) {
        memberQueryService.search(memberId);
        int year = startDate.getYear();
        startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return startDate
                .datesUntil(endDate.plusDays(1), Period.ofMonths(1))
                .map(startDayOfMonth -> calcMonthCalories(memberId, startDayOfMonth))
                .toList();
    }

    public int calcTotalCalorie(List<CaloriesOfMealType> caloriesOfMealTypes) {
        return caloriesOfMealTypes.parallelStream()
                .reduce(
                        0,
                        (accumulator, mealCalorie) ->
                                accumulator
                                        + mealCalorie.breakfast()
                                        + mealCalorie.lunch()
                                        + mealCalorie.dinner()
                                        + mealCalorie.snack(),
                        Integer::sum);
    }

    private List<Meal> findAllMealOfMealLog(
            Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return findMealLog(memberId, startDateTime, endDateTime).stream()
                .map(MealLog::getMeals)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<MealLog> findMealLog(
            Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return mealLogRepository.findAllByMemberIdAndCreatedAtBetween(
                memberId, startDateTime, endDateTime);
    }

    private IntakeNutrients sumIntakeNutrients(List<Meal> meals) {
        int totalCalorie = 0;
        int totalCarbs = 0;
        int totalProtein = 0;
        int totalFat = 0;
        for (Meal meal : meals) {
            totalCalorie += meal.getCalorie();
            totalCarbs += meal.getCarbs();
            totalProtein += meal.getProtein();
            totalFat += meal.getFat();
        }
        return new IntakeNutrients(totalCalorie, totalCarbs, totalProtein, totalFat);
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

    private List<LocalDate> getStartDatesOfWeeks(LocalDate startDate, LocalDate endDate) {
        LocalDate startDayOfFirstWeek = startDate.with(WeekFields.of(Locale.KOREA).dayOfWeek(), 1);
        LocalDate lastDayOfLastWeek = endDate.with(WeekFields.of(Locale.KOREA).dayOfWeek(), 7);
        return startDayOfFirstWeek
                .datesUntil(lastDayOfLastWeek.plusDays(1), Period.ofWeeks(1))
                .toList();
    }

    private CaloriesOfMealType calcWeekCalories(Long memberId, LocalDate startDayOfWeek) {
        LocalDate endDayOfWeek = startDayOfWeek.plusDays(6);
        LocalDateTime startDateTime = startDayOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endDayOfWeek.atTime(LocalTime.MAX);
        List<MealLog> mealLogs = findMealLog(memberId, startDateTime, endDateTime);
        return sumCalorieByMealType(mealLogs);
    }

    private CaloriesOfMealType calcMonthCalories(Long memberId, LocalDate startDayOfMonth) {
        LocalDate endDayOfMonth = YearMonth.from(startDayOfMonth).atEndOfMonth();
        LocalDateTime startDateTime = startDayOfMonth.atStartOfDay();
        LocalDateTime endDateTime = endDayOfMonth.atTime(LocalTime.MAX);
        List<MealLog> mealLogs = findMealLog(memberId, startDateTime, endDateTime);
        return sumCalorieByMealType(mealLogs);
    }
}
