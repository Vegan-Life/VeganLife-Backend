package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogModifyRequest;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.mapper.MealImageMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealMapper;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.IntakeNotifyService;
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
    private final MealLogQueryService mealLogQueryService;
    private final IntakeNotifyService intakeNotifyService;
    private final MealLogRepository mealLogRepository;

    private final MealLogMapper mealLogMapper;
    private final MealMapper mealMapper;
    private final MealImageMapper mealImageMapper;

    public void add(MealLogAddRequest request, Long memberId) {
        MealLog mealLog =
                mealLogMapper.toEntity(request.mealType(), memberQueryService.search(memberId));
        modifyMeals(request.meals(), mealLog);
        modifyMealImages(request.imageUrls(), mealLog);
        mealLogRepository.save(mealLog);
        intakeNotifyService.notifyIfOverIntake(memberId);
    }

    public void modify(Long mealLogId, MealLogModifyRequest request) {
        MealLog mealLog = mealLogQueryService.search(mealLogId);
        modifyMeals(request.meals(), mealLog);
        modifyMealImages(request.imageUrls(), mealLog);
        mealLogRepository.flush();
        intakeNotifyService.notifyIfOverIntake(mealLog.getMember().getId());
    }

    public void remove(Long mealLogId) {

        MealLog mealLog = mealLogQueryService.search(mealLogId);
        mealLogRepository.delete(mealLog);
    }

    public void removeAll(Long memberId) {
        Member member = memberQueryService.search(memberId);
        mealLogRepository.deleteAllByMemberId(member.getId());
    }

    private void modifyMeals(List<MealAddRequest> requests, MealLog mealLog) {
        List<Meal> meals =
                requests.stream()
                        .map(
                                request ->
                                        mealMapper.toEntity(
                                                request,
                                                mealLog,
                                                mealDataQueryService.search(request.mealDataId())))
                        .toList();
        mealLog.modifyMeals(meals);
    }

    private void modifyMealImages(List<String> requests, MealLog mealLog) {
        List<MealImage> mealImages =
                requests.stream()
                        .map(request -> mealImageMapper.toEntity(request, mealLog))
                        .toList();
        mealLog.modifyMealImages(mealImages);
    }
}
