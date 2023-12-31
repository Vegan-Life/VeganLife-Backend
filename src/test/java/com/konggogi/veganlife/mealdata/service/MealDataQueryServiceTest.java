package com.konggogi.veganlife.mealdata.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.OwnerType;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import com.konggogi.veganlife.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@ExtendWith(MockitoExtension.class)
public class MealDataQueryServiceTest {

    @Mock MealDataRepository mealDataRepository;
    @InjectMocks MealDataQueryService mealDataQueryService;

    Member member = Member.builder().email("test123@test.com").build();

    @Test
    @DisplayName("키워드를 포함하는 이름을 가진 식품 데이터들을 조회")
    void searchByKeywordTest() {
        // given
        List<MealData> mealDataList =
                List.of(
                        MealDataFixture.TOTAL_AMOUNT.getWithNameAndOwnerType(
                                "통밀빵", OwnerType.ALL, member),
                        MealDataFixture.TOTAL_AMOUNT.getWithNameAndOwnerType(
                                "통밀크래커", OwnerType.ALL, member));
        Page<MealData> found =
                PageableExecutionUtils.getPage(
                        mealDataList, Pageable.ofSize(20), mealDataList::size);
        given(
                        mealDataRepository.findByNameContainingAndOwnerType(
                                anyString(), any(OwnerType.class), any(Pageable.class)))
                .willReturn(found);
        // when
        Page<MealData> result =
                mealDataQueryService.searchByKeyword("통", OwnerType.ALL, Pageable.ofSize(12));
        // then
        assertThat(result).hasSize(2);
        assertThat(result.map(MealData::getName)).allMatch(name -> name.contains("통"));
        assertThat(result.map(MealData::getOwnerType))
                .allMatch(ownerType -> ownerType.equals(OwnerType.ALL));
    }

    @Test
    @DisplayName("식품 데이터 ID에 해당하는 식품 데이터 상세 조회")
    void searchTest() {
        // given
        MealData found = MealDataFixture.TOTAL_AMOUNT.getWithName(1L, "통밀빵", member);
        given(mealDataRepository.findById(anyLong())).willReturn(Optional.of(found));
        // when
        MealData result = mealDataQueryService.search(found.getId());
        // then
        assertThat(result.getName()).isEqualTo("통밀빵");
    }

    @Test
    @DisplayName("식품 id에 해당하는 식품 데이터를 찾지 못했을 때 예외")
    void searchNotFoundExceptionTest() {
        // given
        given(mealDataRepository.findById(anyLong())).willReturn(Optional.empty());
        // when
        assertThatThrownBy(() -> mealDataQueryService.search(0L))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEAL_DATA.getDescription());
    }
}
