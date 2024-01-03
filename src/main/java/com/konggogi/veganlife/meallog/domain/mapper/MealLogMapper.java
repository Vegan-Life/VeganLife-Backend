package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.service.dto.MealLogDetailsDto;
import com.konggogi.veganlife.meallog.service.dto.MealLogListDto;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = MealMapper.class)
public interface MealLogMapper {

    @Mapping(target = "id", ignore = true)
    MealLog toEntity(MealType mealType, Member member);

    @Mapping(source = "mealLogDto", target = "thumbnailUrl", qualifiedByName = "getThumbnailUrl")
    @Mapping(source = "mealLogDto", target = "totalCalorie", qualifiedByName = "getTotalCalorie")
    MealLogListResponse toMealLogListResponse(MealLogListDto mealLogDto);

    @Mapping(
            source = "mealLogDetailsDto.mealImages",
            target = "imageUrls",
            qualifiedByName = "mealImageToImageUrl")
    @Mapping(
            source = "mealLogDetailsDto",
            target = "totalIntakeNutrients",
            qualifiedByName = "getTotalIntakeNutrients")
    MealLogDetailsResponse toMealLogDetailsResponse(MealLogDetailsDto mealLogDetailsDto);

    MealLogListDto toMealLogListDto(MealLog mealLog);

    MealLogDetailsDto toMealDetailsDto(MealLog mealLog);

    @Named("mealImageToImageUrl")
    static String mealImageToImageUrl(MealImage mealImage) {

        return mealImage.getImageUrl();
    }

    @Named("getThumbnailUrl")
    static String getThumbnailUrl(MealLogListDto mealLogDto) {

        return mealLogDto.getThumbnailUrl();
    }

    @Named("getTotalCalorie")
    static Integer getTotalCalorie(MealLogListDto mealLogDto) {

        return mealLogDto.getTotalCalorie();
    }

    @Named("getTotalIntakeNutrients")
    static IntakeNutrients getTotalIntakeNutrients(MealLogDetailsDto mealLogDetailsDto) {

        return mealLogDetailsDto.getTotalIntakeNutrients();
    }
}
