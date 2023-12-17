package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealMapper;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MealLogService {

    private final MemberQueryService memberQueryService;
    private final MealDataQueryService mealDataQueryService;
    private final MealLogRepository mealLogRepository;
    private final MealLogMapper mealLogMapper;
    private final MealMapper mealMapper;

    public void add(MealLogAddRequest mealLogAddRequest, Long memberId) {

        mealLogRepository.save(
                mealLogMapper.toEntity(
                        mealLogAddRequest.mealType(),
                        mealLogAddRequest.meals().stream().map(this::toMeal).toList(),
                        memberQueryService.search(memberId)));
    }

    private Meal toMeal(MealAddRequest mealAddRequest) {

        return mealMapper.mealAddRequestToEntity(
                mealAddRequest, mealDataQueryService.search(mealAddRequest.mealDataId()));
    }
}
