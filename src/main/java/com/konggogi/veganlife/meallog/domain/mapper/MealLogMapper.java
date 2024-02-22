package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.domain.Member;
import jakarta.persistence.Tuple;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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

    default Map<MealType, Integer> toTotalCaloriesOfMealTypeMap(
            List<Tuple> caloriesOfMealTypeTuples) {
        Map<MealType, Integer> caloriesOfMealTypeMap = new EnumMap<>(MealType.class);
        for (Tuple tuple : caloriesOfMealTypeTuples) {
            MealType mealType = tuple.get(0, MealType.class);
            Integer totalCalories = tuple.get(1, Long.class).intValue();
            caloriesOfMealTypeMap.put(mealType, totalCalories);
        }
        return caloriesOfMealTypeMap;
    }

    @Named("mealImageToImageUrl")
    static String mealImageToImageUrl(MealImage mealImage) {

        return mealImage.getImageUrl();
    }
}
