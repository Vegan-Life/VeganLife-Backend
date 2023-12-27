package com.konggogi.veganlife.mealdata.domain.mapper;


import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataAddRequest;
import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataDetailsResponse;
import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataListResponse;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.MealDataType;
import com.konggogi.veganlife.mealdata.domain.OwnerType;
import com.konggogi.veganlife.member.domain.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MealDataMapper {

    MealDataListResponse toMealDataListResponse(MealData mealData);

    MealDataDetailsResponse toMealDataDetailsResponse(MealData mealData);

    default MealData toEntity(MealDataAddRequest request, Member member) {

        MealData mealData =
                MealData.builder()
                        .name(request.name())
                        .type(MealDataType.AMOUNT_PER_SERVE)
                        .amount(request.amount())
                        .amountPerServe(request.amountPerServe())
                        .intakeUnit(request.intakeUnit())
                        .ownerType(OwnerType.MEMBER)
                        .member(member)
                        .build();
        mealData.setNutrientsPerUnit(
                request.caloriePerServe(),
                request.carbsPerServe(),
                request.proteinPerServe(),
                request.fatPerServe());
        return mealData;
    }
}
