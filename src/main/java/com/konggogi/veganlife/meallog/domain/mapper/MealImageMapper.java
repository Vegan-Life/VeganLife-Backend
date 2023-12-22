package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.domain.MealImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MealImageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealLog", ignore = true)
    MealImage toEntity(String imageUrl);
}
