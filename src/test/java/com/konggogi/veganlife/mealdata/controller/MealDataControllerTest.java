package com.konggogi.veganlife.mealdata.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.AccessType;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.PersonalMealData;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataDtoMapper;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.fixture.PersonalMealDataFixture;
import com.konggogi.veganlife.mealdata.service.MealDataSearchService;
import com.konggogi.veganlife.mealdata.service.dto.MealDataDetailsDto;
import com.konggogi.veganlife.mealdata.service.dto.MealDataListDto;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MealDataController.class)
public class MealDataControllerTest extends RestDocsTest {

    @MockBean MealDataSearchService mealDataSearchService;
    @Autowired MealDataDtoMapper mealDataDtoMapper;

    @Test
    @DisplayName("키워드 기반 식품 데이터 목록 검색 API")
    void getMealDataListTest() throws Exception {

        // given
        List<MealData> all =
                Stream.of("통밀빵", "통밀크래커").map(MealDataFixture.MEAL::getWithName).toList();
        List<PersonalMealData> personal =
                Stream.of("참치 통조림").map(PersonalMealDataFixture.MEAL::getWithName).toList();
        List<MealDataListDto> result =
                Stream.concat(
                                all.stream().map(MealDataListDto::fromMealData),
                                personal.stream().map(MealDataListDto::fromPersonalMealData))
                        .sorted(Comparator.comparing(MealDataListDto::name))
                        .toList();

        given(mealDataSearchService.searchByKeyword(anyString(), any(Pageable.class)))
                .willReturn(result);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/meal-data")
                                .headers(authorizationHeader())
                                .queryParam("keyword", "통")
                                .queryParam("page", "0")
                                .queryParam("size", "12"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("참치 통조림"))
                .andExpect(jsonPath("$[0].accessType").value(AccessType.PERSONAL.name()))
                .andExpect(jsonPath("$[1].name").value("통밀빵"))
                .andExpect(jsonPath("$[1].accessType").value(AccessType.ALL.name()))
                .andExpect(jsonPath("$[2].name").value("통밀크래커"))
                .andExpect(jsonPath("$[2].accessType").value(AccessType.ALL.name()))
                .andExpect(jsonPath("$.size()").value(3));

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-data-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        parameterWithName("keyword").description("검색을 위한 키워드"),
                                        pageDesc(),
                                        sizeDesc())));
    }

    @Test
    @DisplayName("ID 기반 식품 데이터 상세 조회 API")
    void getMealDataDetailsTest() throws Exception {
        // given
        MealData mealData = MealDataFixture.MEAL.get();
        MealDataDetailsDto result = MealDataDetailsDto.fromMealData(mealData);
        given(mealDataSearchService.search(anyLong(), any(AccessType.class))).willReturn(result);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/meal-data/{id}", mealData.getId())
                                .headers(authorizationHeader())
                                .queryParam("accessType", "ALL"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(mealData.getName()))
                .andExpect(jsonPath("$.type").value(mealData.getType().name()));

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-data-details",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("id").description("식품 데이터 id")),
                                queryParameters(
                                        parameterWithName("accessType")
                                                .description(
                                                        "ALL: 데이터셋 데이터, PERSONAL: 사용자 추가 데이터"))));
    }

    @Test
    @DisplayName("식품 데이터 상세 조회 API NotFound 예외")
    void getMealDataDetailsNotFoundExceptionTest() throws Exception {
        // given
        given(mealDataSearchService.search(anyLong(), any(AccessType.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.MEAL_DATA_NOT_FOUND));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/meal-data/{id}", -999L)
                                .headers(authorizationHeader())
                                .queryParam("accessType", "ALL"));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("meal-data-details-not-found", getDocumentResponse()));
    }
}
