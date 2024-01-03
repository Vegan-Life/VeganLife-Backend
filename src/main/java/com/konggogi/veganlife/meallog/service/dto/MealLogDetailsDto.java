package com.konggogi.veganlife.meallog.service.dto;


import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.util.List;
import java.util.function.ToIntFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record MealLogDetailsDto(
        Long id,
        MealType mealType,
        List<MealDetailsDto> meals,
        List<MealImage> mealImages,
        Member member) {

    public String getThumbnailUrl() {
        if (!mealImages.isEmpty()) {
            return mealImages.get(0).getImageUrl();
        }
        return null;
    }

    public IntakeNutrients getTotalIntakeNutrients() {
        // TODO: reduce를 사용했을 때와 성능 비교해보기
        return new IntakeNutrients(
                calculateTotal(MealDetailsDto::calorie, meals),
                calculateTotal(MealDetailsDto::carbs, meals),
                calculateTotal(MealDetailsDto::protein, meals),
                calculateTotal(MealDetailsDto::fat, meals));
    }

    private Integer calculateTotal(ToIntFunction<MealDetailsDto> func, List<MealDetailsDto> meals) {

        return meals.stream().mapToInt(func).sum();
    }
}
