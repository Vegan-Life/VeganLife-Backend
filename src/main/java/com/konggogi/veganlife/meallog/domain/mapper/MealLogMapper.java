package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.service.dto.MealLogDetails;
import com.konggogi.veganlife.meallog.service.dto.MealLogList;
import com.konggogi.veganlife.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = MealMapper.class)
public interface MealLogMapper {

    @Mapping(target = "id", ignore = true)
    MealLog toEntity(MealType mealType, Member member);

    @Mapping(source = "mealLogList.mealLog.id", target = "id")
    @Mapping(source = "mealLogList.mealLog.mealType", target = "mealType")
    MealLogListResponse toMealLogListResponse(MealLogList mealLogList);

    @Mapping(source = "mealLogDetails.mealLog.id", target = "mealLogId")
    @Mapping(source = "mealLogDetails.mealLog.mealType", target = "mealType")
    @Mapping(source = "mealLogDetails.intakeNutrients", target = "intakeNutrients")
    @Mapping(
            source = "mealLogDetails.mealImages",
            target = "imageUrls",
            qualifiedByName = "mealImageToImageUrl")
    MealLogDetailsResponse toMealLogDetailsResponse(MealLogDetails mealLogDetails);

    @Named("mealImageToImageUrl")
    static String mealImageToImageUrl(MealImage mealImage) {

        return mealImage.getImageUrl();
    }
}
