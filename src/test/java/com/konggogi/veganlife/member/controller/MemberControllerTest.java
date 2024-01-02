package com.konggogi.veganlife.member.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.controller.dto.request.SignupRequest;
import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.exception.DuplicatedNicknameException;
import com.konggogi.veganlife.member.fixture.CaloriesOfMealTypeFixture;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.member.service.NutrientsQueryService;
import com.konggogi.veganlife.member.service.dto.CaloriesOfMealType;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends RestDocsTest {
    @MockBean MemberService memberService;
    @MockBean MemberQueryService memberQueryService;
    @MockBean NutrientsQueryService nutrientsQueryService;

    @Test
    @DisplayName("회원 정보 수정 API")
    void modifyMemberInfoTest() throws Exception {
        // given
        String nickname = "테스트유저";
        Member member = MemberFixture.DEFAULT_M.getMemberWithName(nickname);
        SignupRequest request =
                new SignupRequest(nickname, Gender.M, VegetarianType.LACTO, 1990, 180, 83);
        given(memberService.modifyMemberInfo(anyLong(), any(SignupRequest.class)))
                .willReturn(member);
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/members")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(request.nickname()));

        perform.andDo(print())
                .andDo(
                        document(
                                "modify-member-info",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("회원 정보 수정 API - 중복 닉네임 예외 발생")
    void modifyMemberInfoDuplicatedNicknameTest() throws Exception {
        // given
        SignupRequest request =
                new SignupRequest("테스트유저", Gender.M, VegetarianType.LACTO, 1990, 180, 83);
        given(memberService.modifyMemberInfo(anyLong(), any(SignupRequest.class)))
                .willThrow(new DuplicatedNicknameException(ErrorCode.DUPLICATED_NICKNAME));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/members")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isConflict());

        perform.andDo(print())
                .andDo(document("modify-member-info-duplicated-nickname", getDocumentResponse()));
    }

    @Test
    @DisplayName("회원 탈퇴 API")
    void removeMember() throws Exception {
        // when
        ResultActions perform =
                mockMvc.perform(delete("/api/v1/members").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNoContent());

        perform.andDo(print())
                .andDo(
                        document(
                                "remove-member",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("회원 탈퇴 API - 없는 회원 예외 발생")
    void removeNotMember() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(memberService)
                .removeMember(anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(delete("/api/v1/members").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("remove-member-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("회원 프로필 조회 API")
    void getMemberDetailsTest() throws Exception {
        // given
        Member member = MemberFixture.DEFAULT_F.getMember();
        given(memberQueryService.search(anyLong())).willReturn(member);
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/members/profile").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(
                        jsonPath("$.vegetarianType").value(member.getVegetarianType().toString()))
                .andExpect(jsonPath("$.gender").value(member.getGender().toString()))
                .andExpect(jsonPath("$.imageUrl").value(member.getProfileImageUrl()))
                .andExpect(jsonPath("$.birthYear").value(member.getBirthYear()))
                .andExpect(jsonPath("$.height").value(member.getHeight()))
                .andExpect(jsonPath("$.weight").value(member.getWeight()));

        perform.andDo(print())
                .andDo(
                        document(
                                "member-profile",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("회원 프로필 조회 API - 없는 회원 예외 발생")
    void getNotMemberDetailsTest() throws Exception {
        // given
        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/members/profile").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("member-profile-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("회원 프로필 수정 API")
    void modifyMemberProfileTest() throws Exception {
        // given
        Member member = MemberFixture.DEFAULT_F.getMember();
        MemberProfileRequest request =
                new MemberProfileRequest(
                        member.getNickname(),
                        member.getProfileImageUrl(),
                        member.getVegetarianType(),
                        member.getGender(),
                        member.getBirthYear(),
                        member.getHeight(),
                        member.getWeight());
        given(memberService.modifyMemberProfile(anyLong(), any(MemberProfileRequest.class)))
                .willReturn(member);
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/members/profile")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isOk());

        perform.andDo(print())
                .andDo(
                        document(
                                "modify-profile",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("회원 프로필 수정 API - 중복된 닉네임 예외 발생")
    void modifyMemberProfileDuplicatedNicknameTest() throws Exception {
        // given
        MemberProfileRequest request =
                new MemberProfileRequest(
                        "nickname", "imageUrl", VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberService.modifyMemberProfile(anyLong(), any(MemberProfileRequest.class)))
                .willThrow(new DuplicatedNicknameException(ErrorCode.DUPLICATED_NICKNAME));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/members/profile")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isConflict());

        perform.andDo(print())
                .andDo(document("modify-profile-duplicated-nickname", getDocumentResponse()));
    }

    @Test
    @DisplayName("회원 프로필 수정 API - 없는 회원 예외 발생")
    void modifyNotMemberProfileTest() throws Exception {
        // given
        MemberProfileRequest request =
                new MemberProfileRequest(
                        "nickname", "imageUrl", VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberService.modifyMemberProfile(anyLong(), any(MemberProfileRequest.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/members/profile")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("modify-profile-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("권장 섭취량 조회 API")
    void getMemberDailyNutrientsTest() throws Exception {
        // given
        Member member = MemberFixture.DEFAULT_F.getMember();
        given(memberQueryService.search(member.getId())).willReturn(member);
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
    @DisplayName("권장 섭취량 조회 API - 없는 회원 예외 발생")
    void getNotMemberDailyNutrientsTest() throws Exception {
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
    @DisplayName("금일 섭취량 조회 API")
    void getTodayIntakeTest() throws Exception {
        // given
        IntakeNutrients intakeNutrients = new IntakeNutrients(200, 50, 40, 30);
        given(nutrientsQueryService.searchDailyIntakeNutrients(anyLong(), any(LocalDate.class)))
                .willReturn(intakeNutrients);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/today")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2024-01-01"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.calorie").value(intakeNutrients.calorie()))
                .andExpect(jsonPath("$.carbs").value(intakeNutrients.carbs()))
                .andExpect(jsonPath("$.protein").value(intakeNutrients.protein()))
                .andExpect(jsonPath("$.fat").value(intakeNutrients.fat()));

        perform.andDo(print())
                .andDo(
                        document(
                                "today-nutrients",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        parameterWithName("startDate").description("조회할 날짜"))));
    }

    @Test
    @DisplayName("금일 섭취량 조회 API - 없는 회원 예외 발생")
    void getTodayIntakeNotMemberTest() throws Exception {
        // given
        given(nutrientsQueryService.searchDailyIntakeNutrients(anyLong(), any(LocalDate.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/today")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2024-01-01"));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("today-nutrients-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("주간 섭취량 조회 API")
    void getWeeklyIntakeTest() throws Exception {
        // given
        List<CaloriesOfMealType> caloriesOfMealTypes = new ArrayList<>();
        int days = 7;
        for (int i = 0; i < days; i++) {
            caloriesOfMealTypes.add(CaloriesOfMealTypeFixture.DEFAULT.get());
        }
        int caloriePerMealType = caloriesOfMealTypes.get(0).breakfast();
        int totalCalorie = caloriePerMealType * 4 * days;
        given(
                        nutrientsQueryService.searchWeeklyIntakeCalories(
                                anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(caloriesOfMealTypes);
        given(nutrientsQueryService.calcTotalCalorie(anyList())).willReturn(totalCalorie);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/week")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2023-12-17")
                                .queryParam("endDate", "2023-12-23"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalorie").value(totalCalorie))
                .andExpect(jsonPath("$.periodicCalorie[0].breakfast").value(caloriePerMealType))
                .andExpect(jsonPath("$.periodicCalorie[0].lunch").value(caloriePerMealType))
                .andExpect(jsonPath("$.periodicCalorie[0].dinner").value(caloriePerMealType))
                .andExpect(jsonPath("$.periodicCalorie[0].snack").value(caloriePerMealType));

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
    @DisplayName("주간 섭취량 조회 API - 없는 회원 예외 발생")
    void getWeeklyIntakeNotMemberTest() throws Exception {
        // given
        given(
                        nutrientsQueryService.searchWeeklyIntakeCalories(
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
    void getMonthlyIntakeTest() throws Exception {
        // given
        List<CaloriesOfMealType> caloriesOfMealTypes = new ArrayList<>();
        int weeks = 6;
        for (int i = 0; i < weeks; i++) {
            caloriesOfMealTypes.add(CaloriesOfMealTypeFixture.DEFAULT.getWithIntake(100));
        }
        int caloriePerMealType = caloriesOfMealTypes.get(0).breakfast();
        int totalCalorie = caloriePerMealType * 4 * weeks;
        given(nutrientsQueryService.searchMonthlyIntakeCalories(anyLong(), any(LocalDate.class)))
                .willReturn(caloriesOfMealTypes);
        given(nutrientsQueryService.calcTotalCalorie(anyList())).willReturn(totalCalorie);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/month")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2023-12-01"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalorie").value(totalCalorie))
                .andExpect(jsonPath("$.periodicCalorie[0].breakfast").value(caloriePerMealType))
                .andExpect(jsonPath("$.periodicCalorie[0].lunch").value(caloriePerMealType))
                .andExpect(jsonPath("$.periodicCalorie[0].dinner").value(caloriePerMealType))
                .andExpect(jsonPath("$.periodicCalorie[0].snack").value(caloriePerMealType));

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
    @DisplayName("월간 섭취량 조회 API - 없는 회원 예외 발생")
    void getMonthlyIntakeNotMemberTest() throws Exception {
        // given
        given(nutrientsQueryService.searchMonthlyIntakeCalories(anyLong(), any(LocalDate.class)))
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
    void getYearlyIntakeTest() throws Exception {
        // given
        List<CaloriesOfMealType> caloriesOfMealTypes = new ArrayList<>();
        int months = 12;
        for (int i = 0; i < months; i++) {
            caloriesOfMealTypes.add(CaloriesOfMealTypeFixture.DEFAULT.getWithIntake(300));
        }
        int caloriePerMealType = caloriesOfMealTypes.get(0).breakfast();
        int totalCalorie = caloriePerMealType * 4 * months;
        given(nutrientsQueryService.searchYearlyIntakeCalories(anyLong(), any(LocalDate.class)))
                .willReturn(caloriesOfMealTypes);
        given(nutrientsQueryService.calcTotalCalorie(anyList())).willReturn(totalCalorie);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/nutrients/year")
                                .headers(authorizationHeader())
                                .queryParam("startDate", "2023-01-01"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalorie").value(totalCalorie))
                .andExpect(jsonPath("$.periodicCalorie[0].breakfast").value(caloriePerMealType))
                .andExpect(jsonPath("$.periodicCalorie[0].lunch").value(caloriePerMealType))
                .andExpect(jsonPath("$.periodicCalorie[0].dinner").value(caloriePerMealType))
                .andExpect(jsonPath("$.periodicCalorie[0].snack").value(caloriePerMealType));

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
    @DisplayName("연간 섭취량 조회 API - 없는 회원 예외 발생")
    void getYearlyIntakeNotMemberTest() throws Exception {
        // given
        given(nutrientsQueryService.searchYearlyIntakeCalories(anyLong(), any(LocalDate.class)))
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
