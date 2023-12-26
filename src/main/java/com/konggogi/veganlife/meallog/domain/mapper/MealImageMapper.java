package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MealImageMapper {

    @Mapping(target = "id", ignore = true)
    MealImage toEntity(String imageUrl, MealLog mealLog);
}
