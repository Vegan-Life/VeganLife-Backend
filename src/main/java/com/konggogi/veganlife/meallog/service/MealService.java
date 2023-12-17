package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.mapper.MealMapper;
import com.konggogi.veganlife.meallog.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final MealMapper mealMapper;

    public void add(MealAddRequest mealAddRequest, MealLog mealLog, MealData mealData) {

        mealRepository.save(mealMapper.mealAddRequestToEntity(mealAddRequest, mealLog, mealData));
    }
}
