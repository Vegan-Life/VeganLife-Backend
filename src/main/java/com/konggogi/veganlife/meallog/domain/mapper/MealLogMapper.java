package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = MealMapper.class)
public interface MealLogMapper {

    @Mapping(target = "id", ignore = true)
    MealLog toEntity(MealType mealType, Member member);

    @Mapping(source = "mealLog", target = "thumbnailUrl", qualifiedByName = "getThumbnailUrl")
    @Mapping(source = "mealLog", target = "totalCalorie", qualifiedByName = "getTotalCalorie")
    MealLogListResponse toMealLogListResponse(MealLog mealLog);

    @Mapping(
            source = "mealLog.mealImages",
            target = "imageUrls",
            qualifiedByName = "mealImageToImageUrl")
    @Mapping(
            source = "mealLog",
            target = "totalIntakeNutrients",
            qualifiedByName = "getTotalIntakeNutrients")
    MealLogDetailsResponse toMealLogDetailsResponse(MealLog mealLog);

    @Named("mealImageToImageUrl")
    static String mealImageToImageUrl(MealImage mealImage) {

        return mealImage.getImageUrl();
    }

    @Named("getThumbnailUrl")
    static String getThumbnailUrl(MealLog mealLog) {

        return mealLog.getThumbnailUrl();
    }

    @Named("getTotalCalorie")
    static Integer getTotalCalorie(MealLog mealLog) {

        return mealLog.getTotalCalorie();
    }

    @Named("getTotalIntakeNutrients")
    static IntakeNutrients getTotalIntakeNutrients(MealLog mealLog) {

        return mealLog.getTotalIntakeNutrients();
    }
}
