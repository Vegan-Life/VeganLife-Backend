package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.service.dto.MealLogDetails;
import com.konggogi.veganlife.meallog.service.dto.MealLogList;
import com.konggogi.veganlife.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MealMapper.class)
public interface MealLogMapper {

    @Mapping(target = "id", ignore = true)
    MealLog toEntity(MealType mealType, Member member);

    @Mapping(source = "mealLogList.mealLog.id", target = "id")
    @Mapping(source = "mealLogList.mealLog.mealType", target = "mealType")
    MealLogListResponse toMealLogListResponse(MealLogList mealLogList);

    @Mapping(source = "mealLogDetails.mealLog.id", target = "id")
    @Mapping(source = "mealLogDetails.mealLog.mealType", target = "mealType")
    MealLogDetailsResponse toMealLogDetailsResponse(MealLogDetails mealLogDetails);
}
