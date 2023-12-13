package com.konggogi.veganlife.mealdata.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class MealDataQueryServiceTest {

    @Mock MealDataRepository mealDataRepository;
    @InjectMocks MealDataQueryService mealDataQueryService;

    @Test
    @DisplayName("키워드를 포함하는 이름을 가진 식품 데이터들을 조회")
    void searchByKeyword() {
        // given
        List<String> valid = List.of("통밀빵", "통밀크래커");
        Page<MealData> found =
                new PageImpl<>(valid.stream().map(MealDataFixture.MEAL::getWithName).toList());
        given(mealDataRepository.findMealDataByNameContaining(anyString(), any(Pageable.class)))
                .willReturn(found);
        String keyword = "통";
        Pageable pageable = Pageable.ofSize(12);
        // when
        Page<MealData> result = mealDataQueryService.searchByKeyword(keyword, pageable);
        // then
        assertThat(result).hasSize(found.getSize());
    }
}
