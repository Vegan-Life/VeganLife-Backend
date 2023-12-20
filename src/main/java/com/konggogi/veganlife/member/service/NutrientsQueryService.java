package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
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
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(LocalTime.MAX);
        List<Meal> meals = findAllMealOfMealLog(memberId, startDate, endDate);
        return sumIntakeNutrients(meals);
    }

    private List<Meal> findAllMealOfMealLog(
            Long memberId, LocalDateTime startDate, LocalDateTime endDate) {
        return mealLogRepository
                .findAllByMemberIdAndModifiedAtBetween(memberId, startDate, endDate)
                .stream()
                .map(MealLog::getMeals)
                .flatMap(Collection::stream)
                .toList();
    }

    private IntakeNutrients sumIntakeNutrients(List<Meal> meals) {
        IntakeNutrients initIntakeNutrients = new IntakeNutrients(0, 0, 0, 0);
        return meals.parallelStream()
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
}
