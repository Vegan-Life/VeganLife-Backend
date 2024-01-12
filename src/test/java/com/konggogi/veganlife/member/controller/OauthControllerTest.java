package com.konggogi.veganlife.member.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.controller.dto.request.OauthRequest;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import com.konggogi.veganlife.member.exception.UnsupportedProviderException;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.member.service.OauthService;
import com.konggogi.veganlife.member.service.dto.MemberLoginDto;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(OauthController.class)
class OauthControllerTest extends RestDocsTest {
    @MockBean OauthService oauthService;
    @MockBean MemberService memberService;

    @Test
    @DisplayName("소셜 로그인 API")
    void loginTest() throws Exception {
        // given
        Member member = MemberFixture.DEFAULT_F.getOnlyEmailWithId(1L);
        String accessToken = "Bearer accessToken";
        String refreshToken = "Bearer refreshToken";
        OauthRequest oauthRequest = new OauthRequest("oauthAccessToken");
        MemberLoginDto loginDto = new MemberLoginDto(member, accessToken, refreshToken);
        given(oauthService.userAttributesToMember(any(OauthProvider.class), anyString()))
                .willReturn(member);
        given(memberService.login(anyString())).willReturn(loginDto);
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/members/oauth/{provider}/login", "kakao")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(oauthRequest)));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.hasAdditionalInfo").value(false))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));

        perform.andDo(print())
                .andDo(
                        document(
                                "login",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(
                                        parameterWithName("provider")
                                                .description("Oauth 제공자 (naver, kakao)"))));
    }

    @Test
    @DisplayName("소셜 로그인 API - 지원하지 않는 provider 예외 발생")
    void loginUnsupportedProviderTest() throws Exception {
        // given
        OauthRequest oauthRequest = new OauthRequest("oauthAccessToken");
        given(oauthService.userAttributesToMember(any(OauthProvider.class), anyString()))
                .willThrow(new UnsupportedProviderException(ErrorCode.UNSUPPORTED_PROVIDER));

        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/members/oauth/{provider}/login", "kakao")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(oauthRequest)));
        // then
        perform.andExpect(status().isBadRequest());

        perform.andDo(print()).andDo(document("login-unsupported-provider", getDocumentResponse()));
    }
}
