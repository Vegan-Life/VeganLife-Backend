package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.meallog.service.dto.MealLogDetails;
import com.konggogi.veganlife.meallog.service.dto.MealLogList;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.time.LocalDate;
import java.util.List;
import java.util.function.ToIntFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealLogQueryService {

    private final MealLogRepository mealLogRepository;

    public List<MealLogList> searchByDate(LocalDate date, Long memberId) {

        return mealLogRepository.findAllByDate(date, memberId).stream()
                .map(
                        mealLog ->
                                new MealLogList(
                                        mealLog,
                                        getThumbnailUrl(mealLog.getMealImages()),
                                        getTotalCalorie(mealLog.getMeals())))
                .toList();
    }

    public MealLogDetails search(Long id) {

        MealLog mealLog =
                mealLogRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_LOG));
        IntakeNutrients intakeNutrients = getTotalNutrients(mealLog.getMeals());

        return new MealLogDetails(
                mealLog, intakeNutrients, mealLog.getMealImages(), mealLog.getMeals());
    }

    private String getThumbnailUrl(List<MealImage> mealImages) {
        if (!mealImages.isEmpty()) {
            return mealImages.get(0).getImageUrl();
        }
        return null;
    }

    private Integer getTotalCalorie(List<Meal> meals) {

        return calculateTotal(Meal::getCalorie, meals);
    }

    private IntakeNutrients getTotalNutrients(List<Meal> meals) {
        // TODO: reduce를 사용했을 때와 성능 비교해보기
        return new IntakeNutrients(
                calculateTotal(Meal::getCalorie, meals),
                calculateTotal(Meal::getCarbs, meals),
                calculateTotal(Meal::getProtein, meals),
                calculateTotal(Meal::getFat, meals));
    }

    private Integer calculateTotal(ToIntFunction<Meal> func, List<Meal> meals) {

        return meals.stream().mapToInt(func).sum();
    }
}
