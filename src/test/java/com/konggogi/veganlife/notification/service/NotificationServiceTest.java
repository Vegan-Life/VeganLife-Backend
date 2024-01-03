package com.konggogi.veganlife.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.notification.domain.Notification;
import com.konggogi.veganlife.notification.domain.mapper.NotificationMapper;
import com.konggogi.veganlife.notification.exception.SseConnectionException;
import com.konggogi.veganlife.notification.fixture.NotificationFixture;
import com.konggogi.veganlife.notification.repository.EmitterRepository;
import com.konggogi.veganlife.notification.repository.NotificationRepository;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock NotificationRepository notificationRepository;
    @Mock EmitterRepository emitterRepository;
    @Mock MemberQueryService memberQueryService;
    @Spy NotificationMapper notificationMapper;
    @Mock SseEmitter sseEmitter;
    @InjectMocks NotificationService notificationService;

    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Notification notification = NotificationFixture.SSE.get(member);

    @Test
    @DisplayName("SseEmitter 생성")
    void subscribeTest() {
        // given
        Long memberId = member.getId();
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(emitterRepository.findById(anyLong())).willReturn(Optional.of(sseEmitter));
        // when
        SseEmitter sseEmitter = notificationService.subscribe(memberId);
        // then
        assertThat(sseEmitter).isNotNull();
        then(emitterRepository).should().save(anyLong(), any(SseEmitter.class));
    }

    @Test
    @DisplayName("SseEmitter 생성시 회원이 없으면 예외 발생")
    void subscribeNotMemberTest() {
        // given
        Long memberId = member.getId();
        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(() -> notificationService.subscribe(memberId))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
    }

    @Test
    @DisplayName("알림 보내기")
    void sendNotificationTest() {
        // given
        given(emitterRepository.findById(anyLong())).willReturn(Optional.of(sseEmitter));
        // when, then
        assertDoesNotThrow(
                () ->
                        notificationService.sendNotification(
                                member, notification.getType(), notification.getMessage()));
        then(emitterRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("알림을 보낼 때 IOException 발생")
    void sendNotificationIOExceptionTest() throws Exception {
        // given
        given(emitterRepository.findById(anyLong())).willReturn(Optional.of(sseEmitter));
        doThrow(IOException.class).when(sseEmitter).send(any(SseEmitter.SseEventBuilder.class));
        // when, then
        assertThatThrownBy(
                        () ->
                                notificationService.sendNotification(
                                        member, notification.getType(), notification.getMessage()))
                .isInstanceOf(SseConnectionException.class)
                .hasMessageContaining(ErrorCode.SSE_CONNECTION_ERROR.getDescription());
        then(emitterRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("알림을 보낼 때 emitter를 찾을 수 없으면 예외 발생")
    void sendNotificationNotFoundEmitterTest() {
        // given
        given(emitterRepository.findById(anyLong())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(
                        () ->
                                notificationService.sendNotification(
                                        member, notification.getType(), notification.getMessage()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_EMITTER.getDescription());
        then(emitterRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("회원 Id로 Notification 모두 삭제")
    void removeAllTest() {
        // when, then
        assertDoesNotThrow(() -> notificationService.removeAll(member.getId()));
        then(notificationRepository).should().deleteAllByMemberId(anyLong());
    }
}
