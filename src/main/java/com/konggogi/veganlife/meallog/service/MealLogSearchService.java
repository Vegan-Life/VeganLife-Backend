package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealLogSearchService {

    private final MealLogQueryService mealLogQueryService;
    private final MemberQueryService memberQueryService;
    private final MealLogMapper mealLogMapper;

    public List<MealLogListResponse> searchByDate(LocalDate date, Long memberId) {

        Member member = memberQueryService.search(memberId);
        return mealLogQueryService.searchByDateAndMember(date, member).stream()
                .map(mealLogMapper::toMealLogListResponse)
                .toList();
    }

    public MealLogDetailsResponse searchById(Long id) {

        return mealLogMapper.toMealLogDetailsResponse(mealLogQueryService.search(id));
    }
}
