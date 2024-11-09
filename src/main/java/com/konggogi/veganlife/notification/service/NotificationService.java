package com.konggogi.veganlife.notification.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.notification.domain.Notification;
import com.konggogi.veganlife.notification.domain.NotificationMessage;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.domain.mapper.NotificationMapper;
import com.konggogi.veganlife.notification.exception.SseConnectionException;
import com.konggogi.veganlife.notification.repository.EmitterRepository;
import com.konggogi.veganlife.notification.repository.NotificationRepository;
import com.konggogi.veganlife.notification.service.dto.NotificationData;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// TODO: SseService, NotificationService 분리
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60 * 2;
    private static final Long RECONNECTION_TIME = 2000L;
    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final MemberQueryService memberQueryService;
    private final NotificationMapper notificationMapper;

    public SseEmitter subscribe(Long memberId) {
        Member member = memberQueryService.search(memberId);
        SseEmitter emitter = createEmitter(memberId);
        sendNotification(
                member, NotificationType.SSE, NotificationMessage.SSE_CONNECTION.getMessage());
        sendPendingNotifications(memberId);
        return emitter;
    }

    public void removeAll(Long memberId) {
        notificationRepository.deleteAllByMemberId(memberId);
    }

    public void sendNotification(Member member, NotificationType type, String message) {
        Notification notification =
                notificationRepository.save(notificationMapper.toEntity(member, type, message));
        sendToClient(member.getId(), notification);
    }

    public Page<Notification> searchByMember(Long memberId, Pageable pageable) {
        Member member = memberQueryService.search(memberId);
        return notificationRepository.findAllByMember(member.getId(), pageable);
    }

    private void sendToClient(Long memberId, Notification notification) {
        NotificationData data = notificationMapper.toNotificationData(notification);
        emitterRepository
                .findById(memberId)
                .ifPresentOrElse(
                        emitter -> {
                            try {
                                emitter.send(createSseEvent(memberId, data));
                                notification.updateIsSend();
                            } catch (IOException exception) {
                                emitterRepository.deleteById(memberId);
                                throw new SseConnectionException(ErrorCode.SSE_CONNECTION_ERROR);
                            }
                        },
                        () ->
                                log.warn(
                                        "Not Found SseEmitter - Failed to send SSE event to memberId: {}",
                                        memberId));
    }

    private SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
        emitter.onTimeout(() -> emitterRepository.deleteById(memberId));
        return emitter;
    }

    private SseEmitter.SseEventBuilder createSseEvent(Long memberId, NotificationData data) {
        return SseEmitter.event()
                .id(String.valueOf(memberId))
                .data(data)
                .reconnectTime(RECONNECTION_TIME);
    }

    private void sendPendingNotifications(Long memberId) {
        List<Notification> notifications =
                notificationRepository.findAllByMemberIdAndIsSendFalse(memberId);

        notifications.forEach(
                notification -> {
                    NotificationType type = notification.getType();
                    if (type == NotificationType.SSE) return;

                    if (type == NotificationType.INTAKE_OVER_30
                            || type == NotificationType.INTAKE_OVER_60) {
                        if (!LocalDate.now().equals(notification.getCreatedAt().toLocalDate()))
                            return;
                    }
                    sendToClient(memberId, notification);
                });
    }
}
