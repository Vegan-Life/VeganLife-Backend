package com.konggogi.veganlife.member.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.exception.DuplicateNicknameException;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.member.service.NutrientsQueryService;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDate;
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
        MemberInfoRequest request =
                new MemberInfoRequest(nickname, Gender.M, VegetarianType.LACTO, 1990, 180, 83);
        given(memberService.modifyMemberInfo(anyLong(), any(MemberInfoRequest.class)))
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
        MemberInfoRequest request =
                new MemberInfoRequest("테스트유저", Gender.M, VegetarianType.LACTO, 1990, 180, 83);
        given(memberService.modifyMemberInfo(anyLong(), any(MemberInfoRequest.class)))
                .willThrow(new DuplicateNicknameException(ErrorCode.DUPLICATED_NICKNAME));
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
                .willThrow(new DuplicateNicknameException(ErrorCode.DUPLICATED_NICKNAME));
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
}
