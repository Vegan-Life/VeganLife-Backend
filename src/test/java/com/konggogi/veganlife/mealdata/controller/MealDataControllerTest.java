package com.konggogi.veganlife.mealdata.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataAddRequest;
import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.OwnerType;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.mealdata.service.MealDataService;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MealDataController.class)
public class MealDataControllerTest extends RestDocsTest {

    @MockBean MealDataQueryService mealDataQueryService;
    @MockBean MealDataService mealDataService;
    @Autowired MealDataMapper mealDataMapper;

    Member member = Member.builder().email("test123@test.com").build();

    @Test
    @DisplayName("키워드 기반 식품 데이터 목록 검색 API")
    void getMealDataListTest() throws Exception {

        // given
        Pageable pageable = Pageable.ofSize(20);
        List<MealData> mealDataList =
                List.of(
                        MealDataFixture.TOTAL_AMOUNT.getWithName(1L, "통밀빵", member),
                        MealDataFixture.TOTAL_AMOUNT.getWithName(2L, "통밀크래커", member));
        Page<MealData> result =
                PageableExecutionUtils.getPage(mealDataList, pageable, mealDataList::size);

        given(
                        mealDataQueryService.searchByKeyword(
                                anyString(), any(OwnerType.class), any(Pageable.class)))
                .willReturn(result);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/meal-data")
                                .headers(authorizationHeader())
                                .queryParam("keyword", "통")
                                .queryParam("ownerType", "ALL")
                                .queryParam("page", "0")
                                .queryParam("size", "20"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("통밀빵"))
                .andExpect(jsonPath("$.content[1].name").value("통밀크래커"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(20));

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-data-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        parameterWithName("keyword").description("검색을 위한 키워드"),
                                        parameterWithName("ownerType")
                                                .description(
                                                        "ALL: 데이터셋 데이터 검색, MEMBER: 사용자 추가 데이터 검색"),
                                        pageDesc(),
                                        sizeDesc())));
    }

    @Test
    @DisplayName("ID 기반 식품 데이터 상세 조회 API")
    void getMealDataDetailsTest() throws Exception {
        // given
        MealData result = MealDataFixture.TOTAL_AMOUNT.get(1L, member);
        given(mealDataQueryService.search(anyLong())).willReturn(result);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/meal-data/{id}", result.getId())
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(result.getName()))
                .andExpect(jsonPath("$.type").value(result.getType().name()))
                .andExpect(jsonPath("$.amount").value(result.getAmount()))
                .andExpect(jsonPath("$.amountPerServe").value(result.getAmountPerServe()))
                .andExpect(jsonPath("$.caloriePerUnit").value(result.getCaloriePerUnit()))
                .andExpect(jsonPath("$.proteinPerUnit").value(result.getProteinPerUnit()))
                .andExpect(jsonPath("$.fatPerUnit").value(result.getFatPerUnit()))
                .andExpect(jsonPath("$.carbsPerUnit").value(result.getCarbsPerUnit()))
                .andExpect(jsonPath("$.intakeUnit").value(result.getIntakeUnit().name()));

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
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_DATA));
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/meal-data/{id}", 0L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("meal-data-details-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("식품 데이터 등록 API")
    void addMealDataTest() throws Exception {
        MealDataAddRequest request =
                new MealDataAddRequest("통밀빵", 300, 100, 210, 30, 5, 5, IntakeUnit.G);

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/meal-data")
                                .headers(authorizationHeader())
                                .content(toJson(request))
                                .contentType(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isCreated());

        perform.andDo(
                document(
                        "meal-data-add",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("식품 데이터 등록 API - Member Not Found 예외")
    void addMealDataTestMemberNotFoundExceptionTest() throws Exception {
        MealDataAddRequest request =
                new MealDataAddRequest("통밀빵", 300, 100, 210, 30, 5, 5, IntakeUnit.G);

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .given(mealDataService)
                .add(any(MealDataAddRequest.class), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/meal-data")
                                .headers(authorizationHeader())
                                .content(toJson(request))
                                .contentType(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isNotFound());

        perform.andDo(document("meal-data-add-member-not-found", getDocumentResponse()));
    }
}
