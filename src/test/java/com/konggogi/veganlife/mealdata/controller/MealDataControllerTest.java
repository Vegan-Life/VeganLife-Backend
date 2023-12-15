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
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MealDataController.class)
public class MealDataControllerTest extends RestDocsTest {

    @MockBean MealDataQueryService mealDataQueryService;

    @Test
    @DisplayName("키워드 기반 식품 데이터 목록 검색 API")
    void getMealDataListTest() throws Exception {
        // given
        List<String> valid = List.of("통밀빵", "통밀크래커");
        Page<MealData> result =
                new PageImpl<>(valid.stream().map(MealDataFixture.MEAL::getWithName).toList());
        given(mealDataQueryService.searchByKeyword(anyString(), any(Pageable.class)))
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
                .andExpect(jsonPath("$.content[0].name").value("통밀빵"))
                .andExpect(jsonPath("$.content[1].name").value("통밀크래커"))
                .andExpect(jsonPath("$.numberOfElements").value(2));

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
        MealData result = MealDataFixture.MEAL.get();
        given(mealDataQueryService.search(anyLong())).willReturn(result);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/meal-data/{id}", result.getId())
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isOk()).andExpect(jsonPath("$.name").value(result.getName()));

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-data-details",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("id").description("식품 데이터 id"))));
    }

    @Test
    @DisplayName("식품 데이터 상세 조회 API NotFound 예외")
    void getMealDataDetailsNotFoundExceptionTest() throws Exception {
        // given
        given(mealDataQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.MEAL_DATA_NOT_FOUND));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/meal-data/{id}", -999L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("meal-data-details-not-found", getDocumentResponse()));
    }
}
