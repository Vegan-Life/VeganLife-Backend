package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.domain.MealImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MealImageMapper {

    MealImage toEntity(String imageUrl);
}
