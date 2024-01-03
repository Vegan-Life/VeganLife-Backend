package com.konggogi.veganlife.meallog.service.dto;


import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.domain.Member;
import java.util.List;
import java.util.function.ToIntFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record MealLogListDto(
        Long id,
        MealType mealType,
        List<MealListDto> meals,
        List<MealImage> mealImages,
        Member member) {

    public String getThumbnailUrl() {
        if (!mealImages.isEmpty()) {
            return mealImages.get(0).getImageUrl();
        }
        return null;
    }

    public Integer getTotalCalorie() {

        return calculateTotal(MealListDto::calorie, meals);
    }

    private Integer calculateTotal(ToIntFunction<MealListDto> func, List<MealListDto> meals) {

        return meals.stream().mapToInt(func).sum();
    }
}
