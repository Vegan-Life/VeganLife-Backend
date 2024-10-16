package com.konggogi.veganlife.member.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.IntakeCalorieFixture;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.IntakeNutrientsService;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.dto.IntakeCalorie;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(NutrientsController.class)
class NutrientsControllerTest extends RestDocsTest {
    @MockBean MemberQueryService memberQueryService;
    @MockBean IntakeNutrientsService intakeNutrientsService;

    @Test
    @DisplayName("권장 섭취량 조회 API")
    void getRecommendNutrientsTest() throws Exception {
        // given
        Member member = MemberFixture.DEFAULT_M.getWithId(1L);
        given(memberQueryService.search(anyLong())).willReturn(member);
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/members/nutrients").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyCalorie").value(member.getAMR()))
                .andExpect(jsonPath("$.dailyCarbs").value(member.getDailyCarbs()))
                .andExpect(jsonPath("$.dailyProtein").value(member.getDailyProtein()))
                .andExpect(jsonPath("$.dailyFat").value(member.getDailyFat()));

        perform.andDo(print())
                .andDo(
                        document(
                                "recommend-daily-nutrients",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("권장 섭취량 조회 API - Not Found Member")
    void getRecommendNutrientsNotFoundMemberTest() throws Exception {
        // given
        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/members/nutrients").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("recommend-daily-nutrients-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("일일 섭취량 조회 API")
    void getDailyIntakeTest() throws Exception {
        // given
        IntakeNutrients intakeNutrients = new IntakeNutrients(200, 50, 40, 30);
        given(intakeNutrientsService.searchDailyIntakeNutrients(anyLong(), any(LocalDate.class)))
                .willReturn(intakeNutrients);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/day")
                                .headers(authorizationHeader())
                                .queryParam("date", "2024-01-01"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.calorie").value(intakeNutrients.calorie()))
                .andExpect(jsonPath("$.carbs").value(intakeNutrients.carbs()))
                .andExpect(jsonPath("$.protein").value(intakeNutrients.protein()))
                .andExpect(jsonPath("$.fat").value(intakeNutrients.fat()));

        perform.andDo(print())
                .andDo(
                        document(
                                "daily-nutrients",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(parameterWithName("date").description("조회할 날짜"))));
    }

    @Test
    @DisplayName("일일 섭취량 조회 API - Not Found Member")
    void getDailyIntakeNotFoundMemberTest() throws Exception {
        // given
        given(intakeNutrientsService.searchDailyIntakeNutrients(anyLong(), any(LocalDate.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/day")
                                .headers(authorizationHeader())
                                .queryParam("date", "2024-01-01"));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("daily-nutrients-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("주간 섭취량 조회 API")
    void getWeeklyIntakeCalorieTest() throws Exception {
        // given
        List<IntakeCalorie> intakeCalories = new ArrayList<>();
        int days = 7;
        for (int i = 0; i < days; i++) {
            intakeCalories.add(IntakeCalorieFixture.DEFAULT.get());
        }
        int calorie = intakeCalories.get(0).breakfast();
        given(
                        intakeNutrientsService.searchWeeklyIntakeCalories(
                                anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(intakeCalories);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/week")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2023-12-17")
                                .queryParam("endDate", "2023-12-23"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalorie").value((calorie * 4) * days))
                .andExpect(jsonPath("$.periodicCalorie[0].breakfast").value(calorie))
                .andExpect(jsonPath("$.periodicCalorie[0].lunch").value(calorie))
                .andExpect(jsonPath("$.periodicCalorie[0].dinner").value(calorie))
                .andExpect(jsonPath("$.periodicCalorie[0].snack").value(calorie));

        perform.andDo(print())
                .andDo(
                        document(
                                "weekly-intake",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        parameterWithName("startDate").description("조회할 시작 날짜"),
                                        parameterWithName("endDate").description("조회할 마지막 날짜"))));
    }

    @Test
    @DisplayName("주간 섭취량 조회 API - Not Found Member")
    void getWeeklyIntakeCalorieNotFoundMemberTest() throws Exception {
        // given
        given(
                        intakeNutrientsService.searchWeeklyIntakeCalories(
                                anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/week")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2023-12-17")
                                .queryParam("endDate", "2023-12-23"));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("weekly-intake-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("월간 섭취량 조회 API")
    void getMonthlyIntakeCalorieTest() throws Exception {
        // given
        List<IntakeCalorie> intakeCalories = new ArrayList<>();
        int weeks = 5;
        for (int i = 0; i < weeks; i++) {
            intakeCalories.add(IntakeCalorieFixture.DEFAULT.getWithIntake(100));
        }
        int calorie = intakeCalories.get(0).breakfast();
        given(intakeNutrientsService.searchMonthlyIntakeCalories(anyLong(), any(LocalDate.class)))
                .willReturn(intakeCalories);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/month")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2023-12-01"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalorie").value((calorie * 4) * weeks))
                .andExpect(jsonPath("$.periodicCalorie[0].breakfast").value(calorie))
                .andExpect(jsonPath("$.periodicCalorie[0].lunch").value(calorie))
                .andExpect(jsonPath("$.periodicCalorie[0].dinner").value(calorie))
                .andExpect(jsonPath("$.periodicCalorie[0].snack").value(calorie));

        perform.andDo(print())
                .andDo(
                        document(
                                "monthly-intake",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        parameterWithName("startDate")
                                                .description("조회할 달의 시작 날짜"))));
    }

    @Test
    @DisplayName("월간 섭취량 조회 API - Not Found Member")
    void getMonthlyIntakeCalorieNotFoundMemberTest() throws Exception {
        // given
        given(intakeNutrientsService.searchMonthlyIntakeCalories(anyLong(), any(LocalDate.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/month")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2023-12-01"));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("monthly-intake-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("연간 섭취량 조회 API")
    void getYearlyIntakeCalorieTest() throws Exception {
        // given
        List<IntakeCalorie> intakeCalories = new ArrayList<>();
        int months = 12;
        for (int i = 0; i < months; i++) {
            intakeCalories.add(IntakeCalorieFixture.DEFAULT.getWithIntake(300));
        }
        int calorie = intakeCalories.get(0).breakfast();
        given(intakeNutrientsService.searchYearlyIntakeCalories(anyLong(), any(LocalDate.class)))
                .willReturn(intakeCalories);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/year")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2023-01-01"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalorie").value(calorie * 4 * months))
                .andExpect(jsonPath("$.periodicCalorie[0].breakfast").value(calorie))
                .andExpect(jsonPath("$.periodicCalorie[0].lunch").value(calorie))
                .andExpect(jsonPath("$.periodicCalorie[0].dinner").value(calorie))
                .andExpect(jsonPath("$.periodicCalorie[0].snack").value(calorie));

        perform.andDo(print())
                .andDo(
                        document(
                                "yearly-intake",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        parameterWithName("startDate")
                                                .description("조회할 연도의 시작 날짜"))));
    }

    @Test
    @DisplayName("연간 섭취량 조회 API - Not Found Member")
    void getYearlyIntakeCalorieNotFoundMemberTest() throws Exception {
        // given
        given(intakeNutrientsService.searchYearlyIntakeCalories(anyLong(), any(LocalDate.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/year")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2023-01-01"));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("yearly-intake-not-found", getDocumentResponse()));
    }
}
