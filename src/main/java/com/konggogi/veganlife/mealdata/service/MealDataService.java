package com.konggogi.veganlife.mealdata.service;


import com.konggogi.veganlife.global.exception.EntityAccessDeniedException;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataUpdateRequest;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import com.konggogi.veganlife.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MealDataService {

    private final MemberQueryService memberQueryService;
    private final MealDataRepository mealDataRepository;

    private final MealDataMapper mealDataMapper;

    public void add(MealDataUpdateRequest request, Long memberId) {

        mealDataRepository.save(
                mealDataMapper.toEntity(request, memberQueryService.search(memberId)));
    }

    public void removeAll(Long memberId) {
        mealDataRepository.deleteAllByMemberId(memberId);
    }

    public void removeById(Long mealDataId, Long memberId) {

        MealData mealData =
                mealDataRepository
                        .findById(mealDataId)
                        .orElseThrow(
                                () -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_DATA));
        if (!mealData.isOwnedBy(memberId)) {
            throw new EntityAccessDeniedException(ErrorCode.MEAL_DATA_ACCESS_DENIED);
        }
        mealDataRepository.delete(mealData);
    }

    public void modifyById(MealDataUpdateRequest request, Long mealDataId, Long memberId) {

        MealData mealData =
                mealDataRepository
                        .findById(mealDataId)
                        .orElseThrow(
                                () -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_DATA));
        if (!mealData.isOwnedBy(memberId)) {
            throw new EntityAccessDeniedException(ErrorCode.MEAL_DATA_ACCESS_DENIED);
        }
        mealData.modify(
                request.name(),
                request.amount(),
                request.amountPerServe(),
                request.caloriePerServe(),
                request.proteinPerServe(),
                request.fatPerServe(),
                request.carbsPerServe(),
                request.intakeUnit());
    }
}
