package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.mapper.MealImageMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealMapper;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.service.MemberQueryService;
import java.util.List;
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
    private final MealImageMapper mealImageMapper;

    public void add(MealLogAddRequest mealLogAddRequest, Long memberId) {

        MealLog mealLog =
                mealLogMapper.toEntity(
                        mealLogAddRequest.mealType(), memberQueryService.search(memberId));
        addMeals(mealLogAddRequest.meals(), mealLog);
        addMealImages(mealLogAddRequest.imageUrls(), mealLog);
        mealLogRepository.save(mealLog);
    }

    private void addMeals(List<MealAddRequest> mealAddRequests, MealLog mealLog) {
        mealAddRequests.stream()
                .map(
                        request ->
                                mealMapper.toEntity(
                                        request, mealDataQueryService.search(request.mealDataId())))
                .forEach(mealLog::addMeal);
    }

    private void addMealImages(List<String> mealImages, MealLog mealLog) {
        mealImages.stream().map(mealImageMapper::toEntity).forEach(mealLog::addMealImage);
    }
}
