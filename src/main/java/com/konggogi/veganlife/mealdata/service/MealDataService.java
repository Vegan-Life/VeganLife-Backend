package com.konggogi.veganlife.mealdata.service;


import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataAddRequest;
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

    public void add(MealDataAddRequest request, Long memberId) {

        mealDataRepository.save(
                mealDataMapper.toEntity(request, memberQueryService.search(memberId)));
    }

    public void removeAll(Long memberId) {
        mealDataRepository.deleteAllByMemberId(memberId);
    }
}
