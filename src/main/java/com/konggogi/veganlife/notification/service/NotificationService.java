package com.konggogi.veganlife.notification.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
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
        return emitter;
    }

    public void sendNotification(Member member, NotificationType type, String message) {
        Notification notification =
                notificationRepository.save(notificationMapper.toEntity(member, type, message));
        NotificationData notificationData = notificationMapper.toNotificationData(notification);
        sendToClient(member.getId(), notificationData);
    }

    private void sendToClient(Long memberId, Object data) {
        emitterRepository
                .findById(memberId)
                .ifPresentOrElse(
                        emitter -> {
                            try {
                                emitter.send(createSseEvent(memberId, data));
                            } catch (IOException exception) {
                                emitterRepository.deleteById(memberId);
                                throw new SseConnectionException(ErrorCode.SSE_CONNECTION_ERROR);
                            }
                        },
                        () -> {
                            throw new NotFoundEntityException(ErrorCode.NOT_FOUND_EMITTER);
                        });
    }

    private SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
        emitter.onTimeout(() -> emitterRepository.deleteById(memberId));
        return emitter;
    }

    private SseEmitter.SseEventBuilder createSseEvent(Long memberId, Object data) {
        return SseEmitter.event()
                .id(String.valueOf(memberId))
                .data(data)
                .reconnectTime(RECONNECTION_TIME);
    }
}
