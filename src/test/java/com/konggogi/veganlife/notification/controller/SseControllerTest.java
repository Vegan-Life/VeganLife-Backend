package com.konggogi.veganlife.notification.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.notification.domain.Notification;
import com.konggogi.veganlife.notification.exception.SseConnectionException;
import com.konggogi.veganlife.notification.fixture.NotificationFixture;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@WebMvcTest(SseController.class)
class SseControllerTest extends RestDocsTest {

    @MockBean NotificationService notificationService;

    Member member = Member.builder().id(1L).email("test123@test.com").build();
    List<Notification> notifications =
            List.of(
                    NotificationFixture.INTAKE_OVER_30.getWithDate(member, LocalDateTime.now()),
                    NotificationFixture.MENTION.getWithDate(
                            member, LocalDateTime.now().minusMinutes(30)),
                    NotificationFixture.COMMENT_LIKE.getWithDate(
                            member, LocalDateTime.now().minusDays(1)),
                    NotificationFixture.INTAKE_OVER_60.getWithDate(
                            member, LocalDateTime.now().minusDays(2)));

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

    @Test
    @DisplayName("사용자 알림 목록 조회")
    void getNotificationListTest() throws Exception {

        Page<Notification> notificationPage =
                PageableExecutionUtils.getPage(
                        notifications, Pageable.ofSize(20), notifications::size);
        given(notificationService.searchByMember(member.getId(), Pageable.ofSize(20)))
                .willReturn(notificationPage);

        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/sse/notifications")
                                .headers(authorizationHeader())
                                .queryParam("page", "0")
                                .queryParam("size", "20"));

        perform.andExpect(status().isOk()).andExpect(jsonPath("$.content.size()").value(4));

        perform.andDo(print())
                .andDo(
                        document(
                                "sse-get-notification-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(pageDesc(), sizeDesc())));
    }

    @Test
    @DisplayName("사용자 알림 목록 조회 실패 - 존재하지 않는 Member")
    void getNotificationListTestMemberNotFound() throws Exception {

        given(notificationService.searchByMember(member.getId(), Pageable.ofSize(20)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));

        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/sse/notifications")
                                .headers(authorizationHeader())
                                .queryParam("page", "0")
                                .queryParam("size", "20"));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(
                        document(
                                "sse-get-notification-list-member-not-found",
                                getDocumentRequest(),
                                getDocumentResponse()));
    }
}
