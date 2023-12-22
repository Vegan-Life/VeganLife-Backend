package com.konggogi.veganlife.meallog.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.domain.mapper.MealImageMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealImageMapperImpl;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapperImpl;
import com.konggogi.veganlife.meallog.domain.mapper.MealMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealMapperImpl;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MealLogServiceTest {

    @Mock MemberQueryService memberQueryService;
    @Mock MealDataQueryService mealDataQueryService;
    @Mock MealLogRepository mealLogRepository;
    @Spy MealLogMapper mealLogMapper = new MealLogMapperImpl();
    @Spy MealMapper mealMapper = new MealMapperImpl();
    @Spy MealImageMapper mealImageMapper = new MealImageMapperImpl();
    @InjectMocks MealLogService mealLogService;

    Member member = Member.builder().id(1L).email("test123@test.com").build();
    List<MealData> mealData =
            List.of(
                    MealDataFixture.MEAL.get(1L, member),
                    MealDataFixture.MEAL.get(2L, member),
                    MealDataFixture.MEAL.get(3L, member));
    List<MealAddRequest> mealAddRequests =
            mealData.stream()
                    .map(
                            m ->
                                    new MealAddRequest(
                                            "테스트", 100, IntakeUnit.G, 100, 10, 10, 10, m.getId()))
                    .toList();

    List<String> imageUrls = List.of("image1.png", "image2.png", "image3.png");

    @Test
    @DisplayName("식사 기록 저장")
    void mealLogAddTest() {
        // given
        MealLogAddRequest mealLogAddRequest =
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests, imageUrls);
        given(memberQueryService.search(1L)).willReturn(member);
        mealData.forEach(m -> given(mealDataQueryService.search(m.getId())).willReturn(m));
        // when
        mealLogService.add(mealLogAddRequest, member.getId());
        // then
        then(memberQueryService).should(times(1)).search(member.getId());
        mealData.forEach(m -> then(mealDataQueryService).should(times(1)).search(m.getId()));
        then(mealLogRepository).should(times(1)).save(any(MealLog.class));
    }
}
