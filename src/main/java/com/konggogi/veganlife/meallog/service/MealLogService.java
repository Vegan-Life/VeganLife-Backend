package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MealLogService {

    private final MealLogRepository mealLogRepository;
    private final MealLogMapper mealLogMapper;

    public MealLog add(MealLogAddRequest mealLogAddRequest, Member member) {

        return mealLogRepository.save(
                mealLogMapper.mealLogAddRequestToEntity(mealLogAddRequest, member));
    }
}
