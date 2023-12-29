package com.konggogi.veganlife.notification.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.notification.exception.SseConnectionException;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@WebMvcTest(SseController.class)
class SseControllerTest extends RestDocsTest {
    @MockBean NotificationService notificationService;

    @Test
    @DisplayName("SSE 구독 API")
    void subscribeTest() throws Exception {
        // given
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        given(notificationService.subscribe(anyLong())).willReturn(sseEmitter);
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/sse/subscribe").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isOk());

        perform.andDo(print())
                .andDo(
                        document(
                                "sse-subscribe",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("SSE 구독 API - Not Found Member")
    void subscribeNotFoundMemberTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(notificationService)
                .subscribe(anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/sse/subscribe").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("sse-subscribe-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("SSE 구독 API - Not Found Emitter")
    void subscribeNotFoundEmitterTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_EMITTER))
                .when(notificationService)
                .subscribe(anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/sse/subscribe").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("sse-subscribe-not-found-emitter", getDocumentResponse()));
    }

    @Test
    @DisplayName("SSE 구독 API - Sse Connection Fail")
    void subscribeConnectionFailTest() throws Exception {
        // given
        doThrow(new SseConnectionException(ErrorCode.SSE_CONNECTION_ERROR))
                .when(notificationService)
                .subscribe(anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/sse/subscribe").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isServiceUnavailable());

        perform.andDo(print())
                .andDo(document("sse-subscribe-connection-fail", getDocumentResponse()));
    }
}
