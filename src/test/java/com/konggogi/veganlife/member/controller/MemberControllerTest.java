package com.konggogi.veganlife.member.controller;

import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends RestDocsTest {
    @MockBean MemberService memberService;
    @MockBean MemberQueryService memberQueryService;

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
    @DisplayName("회원 정보 조회 API")
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
                                "remove-member",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }
}
