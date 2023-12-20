package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.domain.MealLog;
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

        MealLog mealLog =
                mealLogMapper.toEntity(
                        mealLogAddRequest.mealType(), memberQueryService.search(memberId));
        mealLogAddRequest.meals().stream()
                .map(
                        request ->
                                mealMapper.mealAddRequestToEntity(
                                        request, mealDataQueryService.search(request.mealDataId())))
                .forEach(mealLog::addMeal);
        mealLogRepository.save(mealLog);
    }
}
