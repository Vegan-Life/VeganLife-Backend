package com.konggogi.veganlife.member.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.security.exception.MismatchTokenException;
import com.konggogi.veganlife.global.util.JwtUtils;
import com.konggogi.veganlife.member.controller.dto.request.ReissueRequest;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends RestDocsTest {
    @MockBean MemberQueryService memberQueryService;
    @MockBean JwtUtils jwtUtils;

    private final String refreshToken = "refreshToken";

    @Test
    @DisplayName("accessToken 재발급 API")
    void reissueTokenTest() throws Exception {
        // given
        String accessToken = "accessToken";
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(jwtUtils.extractBearerToken(refreshToken)).willReturn(Optional.of(refreshToken));
        given(memberQueryService.reissueToken(refreshToken)).willReturn(accessToken);
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/auth/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isOk());

        perform.andDo(print())
                .andDo(document("reissue-token", getDocumentRequest(), getDocumentResponse()));
    }

    @Test
    @DisplayName("accessToken 재발급 API - 유효하지 않은 refreshToken 예외 발생")
    void reissueTokenInvalidTest() throws Exception {
        // given
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(jwtUtils.extractBearerToken(refreshToken))
                .willThrow(new InvalidJwtException(ErrorCode.INVALID_TOKEN));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/auth/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isUnauthorized());

        perform.andDo(print())
                .andDo(document("reissue-token-invalid-token", getDocumentResponse()));
    }

    @Test
    @DisplayName("accessToken 재발급 API - refreshToken 불일치 예외 발생")
    void reissueTokenMismatchTest() throws Exception {
        // given
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(jwtUtils.extractBearerToken(refreshToken)).willReturn(Optional.of(refreshToken));
        given(memberQueryService.reissueToken(refreshToken))
                .willThrow(new MismatchTokenException(ErrorCode.MISMATCH_REFRESH_TOKEN));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/auth/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isBadRequest());

        perform.andDo(print()).andDo(document("reissue-token-mismatch", getDocumentResponse()));
    }

    @Test
    @DisplayName("accessToken 재발급 API - 없는 refreshToken 예외 발생")
    void reissueTokenNotFoundRefreshTokenTest() throws Exception {
        // given
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(jwtUtils.extractBearerToken(refreshToken)).willReturn(Optional.of(refreshToken));
        given(memberQueryService.reissueToken(refreshToken))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_REFRESH_TOKEN));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/auth/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("reissue-token-not-found-token", getDocumentResponse()));
    }

    @Test
    @DisplayName("accessToken 재발급 API - 없는 회원 예외 발생")
    void reissueTokenNotFoundMemberTest() throws Exception {
        // given
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(jwtUtils.extractBearerToken(refreshToken)).willReturn(Optional.of(refreshToken));
        given(memberQueryService.reissueToken(refreshToken))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/auth/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("reissue-token-not-found-member", getDocumentResponse()));
    }
}
