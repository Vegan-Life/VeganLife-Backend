package com.konggogi.veganlife.mealdata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.konggogi.veganlife.config.MapStructConfig;
import com.konggogi.veganlife.mealdata.domain.AccessType;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.PersonalMealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.fixture.PersonalMealDataFixture;
import com.konggogi.veganlife.mealdata.service.dto.MealDataDetailsDto;
import com.konggogi.veganlife.mealdata.service.dto.MealDataListDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@Import(MapStructConfig.class)
public class MealDataSearchServiceTest {

    @Mock MealDataQueryService mealDataQueryService;
    @Mock PersonalMealDataQueryService personalMealDataQueryService;
    @InjectMocks MealDataSearchService mealDataSearchService;

    @Test
    @DisplayName("키워드를 포함하는 이름을 가진 식품 데이터들을 조회")
    void searchByKeywordTest() {
        // given
        List<String> all = List.of("통밀빵", "통밀크래커");
        List<String> personal = List.of("참치 통조림");
        List<MealData> resultAll = all.stream().map(MealDataFixture.MEAL::getWithName).toList();
        List<PersonalMealData> resultPersonal =
                personal.stream().map(PersonalMealDataFixture.MEAL::getWithName).toList();
        given(mealDataQueryService.searchByKeyword(anyString(), any(Pageable.class)))
                .willReturn(resultAll);
        given(personalMealDataQueryService.searchByKeyword(anyString(), any(Pageable.class)))
                .willReturn(resultPersonal);
        // when
        List<MealDataListDto> result =
                mealDataSearchService.searchByKeyword("통", Pageable.ofSize(12));
        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).name()).isEqualTo("참치 통조림");
    }

    @Test
    @DisplayName("식품 데이터 ID에 해당하는 식품 데이터 상세 조회")
    void searchTest() {
        // given
        MealData mealData = MealDataFixture.MEAL.getWithName("통밀빵");
        given(mealDataQueryService.search(anyLong())).willReturn(mealData);
        // when
        MealDataDetailsDto result = mealDataSearchService.search(mealData.getId(), AccessType.ALL);
        // then
        assertThat(result.name()).isEqualTo("통밀빵");
    }
}
