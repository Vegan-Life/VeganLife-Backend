package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MealLogAddService {

    private final MealLogService mealLogService;
    private final MealService mealService;
    private final MemberQueryService memberQueryService;
    private final MealDataQueryService mealDataQueryService;

    public void add(MealLogAddRequest mealLogAddRequest, Long memberId) {

        Member member = memberQueryService.findMemberById(memberId);
        MealLog mealLog = mealLogService.add(mealLogAddRequest, member);

        List<MealAddRequest> mealAddRequests = mealLogAddRequest.mealAddRequests();
        mealAddRequests.forEach(
                mealAddRequest -> {
                    MealData mealData = mealDataQueryService.search(mealAddRequest.mealDataId());
                    mealService.add(mealAddRequest, mealLog, mealData);
                });
    }
}
