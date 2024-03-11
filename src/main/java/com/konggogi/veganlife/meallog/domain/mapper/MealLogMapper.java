package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.TotalCalorieOfMealType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = MealMapper.class)
public interface MealLogMapper {

    @Mapping(target = "id", ignore = true)
    MealLog toEntity(MealType mealType, Member member);

    @Mapping(
            source = "mealLog.thumbnail",
            target = "thumbnailUrl",
            qualifiedByName = "mealImageToImageUrl")
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

    default Map<MealType, Integer> toTotalCalorieOfMealTypeMap(
            List<TotalCalorieOfMealType> totalCalorieOfMealTypes) {
        return totalCalorieOfMealTypes.stream()
                .collect(
                        Collectors.toMap(
                                TotalCalorieOfMealType::getMealType,
                                TotalCalorieOfMealType::getTotalCalorie));
    }

    @Named("mealImageToImageUrl")
    static String mealImageToImageUrl(MealImage mealImage) {

        if (mealImage == null) {
            return null;
        }
        return mealImage.getImageUrl();
    }
}
