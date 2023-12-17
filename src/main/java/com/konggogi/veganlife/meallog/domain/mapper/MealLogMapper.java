package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.domain.Member;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MealLogMapper {

    MealLog toEntity(MealType mealType, List<Meal> meals, Member member);
}
