package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.service.MealLogQueryService;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.CaloriesOfMealType;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import jakarta.persistence.Tuple;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntakeNutrientsService {
    private final MemberQueryService memberQueryService;
    private final MealLogQueryService mealLogQueryService;

    public IntakeNutrients searchDailyIntakeNutrients(Long memberId, LocalDate date) {
        Member member = memberQueryService.search(memberId);
        return sumIntakeNutrients(searchAllMealByMemberAndCreatedAt(member, date));
    }

    public List<CaloriesOfMealType> searchWeeklyIntakeCalories(
            Long memberId, LocalDate startDate, LocalDate endDate) {
        memberQueryService.search(memberId);
        return startDate
                .datesUntil(endDate.plusDays(1))
                .map(
                        date -> {
                            return aggregateDailyCaloriesOfMealType(memberId, date);
                        })
                .toList();
    }

    public List<CaloriesOfMealType> searchMonthlyIntakeCalories(
            Long memberId, LocalDate startDate) {
        memberQueryService.search(memberId);
        startDate = YearMonth.from(startDate).atDay(1);
        LocalDate endDate = YearMonth.from(startDate).atEndOfMonth();
        return getStartDatesOfWeeks(startDate, endDate).stream()
                .map(
                        startDateOfWeek -> {
                            LocalDate endDateOfWeek = startDateOfWeek.plusDays(6);
                            return aggregateCaloriesOfMealTypeForPeriod(
                                    memberId, startDateOfWeek, endDateOfWeek);
                        })
                .toList();
    }

    public List<CaloriesOfMealType> searchYearlyIntakeCalories(Long memberId, LocalDate startDate) {
        memberQueryService.search(memberId);
        int year = startDate.getYear();
        startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return startDate
                .datesUntil(endDate.plusDays(1), Period.ofMonths(1))
                .map(
                        startDateOfMonth -> {
                            LocalDate endDateOfMonth =
                                    YearMonth.from(startDateOfMonth).atEndOfMonth();
                            return aggregateCaloriesOfMealTypeForPeriod(
                                    memberId, startDateOfMonth, endDateOfMonth);
                        })
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

    private List<Meal> searchAllMealByMemberAndCreatedAt(Member member, LocalDate date) {
        return mealLogQueryService.searchByDateAndMember(date, member).stream()
                .map(MealLog::getMeals)
                .flatMap(Collection::stream)
                .toList();
    }

    private IntakeNutrients sumIntakeNutrients(List<Meal> meals) {
        int totalCalorie = meals.stream().mapToInt(Meal::getCalorie).sum();
        int totalCarbs = meals.stream().mapToInt(Meal::getCarbs).sum();
        int totalProtein = meals.stream().mapToInt(Meal::getProtein).sum();
        int totalFat = meals.stream().mapToInt(Meal::getFat).sum();
        return new IntakeNutrients(totalCalorie, totalCarbs, totalProtein, totalFat);
    }

    private CaloriesOfMealType aggregateDailyCaloriesOfMealType(Long memberId, LocalDate date) {
        List<Tuple> caloriesOfMeals =
                mealLogQueryService.sumCaloriesOfMealTypeByMemberIdAndDate(memberId, date);
        return createCaloriesOfMealType(caloriesOfMeals);
    }

    private CaloriesOfMealType aggregateCaloriesOfMealTypeForPeriod(
            Long memberId, LocalDate startDate, LocalDate endDate) {
        List<Tuple> caloriesOfMeals =
                mealLogQueryService.sumCaloriesOfMealTypeByMemberIdAndDateBetween(
                        memberId, startDate, endDate);
        return createCaloriesOfMealType(caloriesOfMeals);
    }

    private CaloriesOfMealType createCaloriesOfMealType(List<Tuple> caloriesOfMealTypeTuples) {
        Map<MealType, Integer> caloriesOfMealTypeMap = new EnumMap<>(MealType.class);
        for (Tuple tuple : caloriesOfMealTypeTuples) {
            MealType mealType = tuple.get("mealType", MealType.class);
            Integer totalCalories = tuple.get("totalCalories", Long.class).intValue();
            caloriesOfMealTypeMap.put(mealType, totalCalories);
        }
        return new CaloriesOfMealType(
                caloriesOfMealTypeMap.getOrDefault(MealType.BREAKFAST, 0),
                caloriesOfMealTypeMap.getOrDefault(MealType.LUNCH, 0),
                caloriesOfMealTypeMap.getOrDefault(MealType.DINNER, 0),
                sumSnackTotalCalorie(caloriesOfMealTypeMap));
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
}
