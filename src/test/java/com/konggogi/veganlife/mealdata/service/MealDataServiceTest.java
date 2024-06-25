package com.konggogi.veganlife.mealdata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataAddRequest;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapperImpl;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MealDataServiceTest {

    @Mock MemberQueryService memberQueryService;
    @Mock MealDataRepository mealDataRepository;
    @Spy MealDataMapper mealDataMapper = new MealDataMapperImpl();
    @InjectMocks MealDataService mealDataService;

    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);

    @Test
    @DisplayName("식품 데이터를 등록")
    void addTest() {

        MealDataAddRequest request = new MealDataAddRequest("통밀빵", 300, 100, 210, 30, 5, 3, "g");

        given(memberQueryService.search(anyLong())).willReturn(member);

        mealDataService.add(request, 1L);
        MealData mealData = mealDataMapper.toEntity(request, member);

        then(memberQueryService).should(times(1)).search(anyLong());
        then(mealDataRepository).should(times(1)).save(any(MealData.class));
        assertThat(mealData.getCaloriePerUnit()).isEqualTo((double) 210 / 100);
        assertThat(mealData.getCarbsPerUnit()).isEqualTo((double) 30 / 100);
        assertThat(mealData.getProteinPerUnit()).isEqualTo((double) 5 / 100);
        assertThat(mealData.getFatPerUnit()).isEqualTo((double) 3 / 100);
    }

    @Test
    @DisplayName("회원 Id로 MealData 모두 삭제")
    void removeAllTest() {
        // when, then
        assertDoesNotThrow(() -> mealDataService.removeAll(member.getId()));
        then(mealDataRepository).should().deleteAllByMemberId(anyLong());
    }
}
