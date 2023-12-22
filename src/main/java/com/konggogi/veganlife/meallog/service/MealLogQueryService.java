package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.meallog.service.dto.MealLogList;
import java.time.LocalDate;
import java.util.List;
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
                                        calculateTotalCalorie(mealLog.getMeals())))
                .toList();
    }

    private String getThumbnailUrl(List<MealImage> mealImages) {
        if (!mealImages.isEmpty()) {
            return mealImages.get(0).getImageUrl();
        }
        return null;
    }

    private Integer calculateTotalCalorie(List<Meal> meals) {

        return meals.stream().mapToInt(Meal::getCalorie).sum();
    }
}
