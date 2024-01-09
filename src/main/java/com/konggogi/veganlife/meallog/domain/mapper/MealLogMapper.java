package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = MealMapper.class)
public interface MealLogMapper {

    @Mapping(target = "id", ignore = true)
    MealLog toEntity(MealType mealType, Member member);

    @Mapping(target = "thumbnailUrl", expression = "java(mealLog.getThumbnailUrl())")
    @Mapping(target = "totalCalorie", expression = "java(mealLog.getTotalCalorie())")
    MealLogListResponse toMealLogListResponse(MealLog mealLog);

    @Mapping(
            source = "mealLog.mealImages",
            target = "imageUrls",
            qualifiedByName = "mealImageToImageUrl")
    @Mapping(
            target = "totalIntakeNutrients",
            expression = "java(mealLog.getTotalIntakeNutrients())")
    MealLogDetailsResponse toMealLogDetailsResponse(MealLog mealLog);

    @Named("mealImageToImageUrl")
    static String mealImageToImageUrl(MealImage mealImage) {

        return mealImage.getImageUrl();
    }
}
