package com.konggogi.veganlife.member.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.security.exception.MismatchTokenException;
import com.konggogi.veganlife.global.util.JwtUtils;
import com.konggogi.veganlife.member.controller.dto.request.ReissueRequest;
import com.konggogi.veganlife.member.service.RefreshTokenService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends RestDocsTest {
    @MockBean RefreshTokenService refreshTokenService;
    @MockBean JwtUtils jwtUtils;
    private final String refreshToken = "Bearer refreshToken";

    @Test
    @DisplayName("AccessToken 재발급 API")
    void reissueTokenTest() throws Exception {
        // given
        String accessToken = "Bearer accessToken";
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(refreshTokenService.reissueAccessToken(anyString())).willReturn(accessToken);
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/auth/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").value(accessToken));

        perform.andDo(print())
                .andDo(document("reissue-token", getDocumentRequest(), getDocumentResponse()));
    }

    @Test
    @DisplayName("AccessToken 재발급 API - Invalid Jwt caused by Fail extract bearerToken")
    void reissueTokenInvalidTest() throws Exception {
        // given
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(refreshTokenService.reissueAccessToken(anyString()))
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
    @DisplayName("accessToken 재발급 API - Invalid Jwt caused by Fail extract email")
    void reissueTokenExtractEmailTest() throws Exception {
        // given
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(refreshTokenService.reissueAccessToken(anyString()))
                .willThrow(new InvalidJwtException(ErrorCode.NOT_FOUND_USER_INFO_TOKEN));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/auth/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isUnauthorized());

        perform.andDo(print())
                .andDo(document("reissue-token-not-found-user-info", getDocumentResponse()));
    }

    @Test
    @DisplayName("accessToken 재발급 API - Not Found Member")
    void reissueTokenNotFoundMemberTest() throws Exception {
        // given
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(refreshTokenService.reissueAccessToken(anyString()))
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

    @Test
    @DisplayName("AccessToken 재발급 API - Not Found RefreshToken")
    void reissueTokenNotFoundRefreshTokenTest() throws Exception {
        // given
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(refreshTokenService.reissueAccessToken(anyString()))
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
    @DisplayName("AccessToken 재발급 API - Mismatch")
    void reissueTokenMismatchTest() throws Exception {
        // given
        ReissueRequest request = new ReissueRequest(refreshToken);
        given(refreshTokenService.reissueAccessToken(anyString()))
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
}
