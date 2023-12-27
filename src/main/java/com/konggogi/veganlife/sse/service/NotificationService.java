package com.konggogi.veganlife.sse.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.sse.domain.NotificationMessage;
import com.konggogi.veganlife.sse.domain.NotificationType;
import com.konggogi.veganlife.sse.exception.SseConnectionException;
import com.konggogi.veganlife.sse.repository.EmitterRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60 * 1000L;
    private static final Long RECONNECTION_TIME = 1000L;
    private final EmitterRepository emitterRepository;

    private final MemberQueryService memberQueryService;

    public SseEmitter subscribe(Long memberId) {
        memberQueryService.search(memberId);
        SseEmitter emitter = createEmitter(memberId);

        sendToClient(memberId, NotificationType.SSE, NotificationMessage.SSE_CONNECTION);
        return emitter;
    }

    private void sendToClient(Long memberId, NotificationType type, Object data) {
        emitterRepository
                .findById(memberId)
                .ifPresent(
                        emitter -> {
                            try {
                                emitter.send(createSseEvent(memberId, type, data));
                            } catch (IOException exception) {
                                emitterRepository.deleteById(memberId);
                                throw new SseConnectionException(ErrorCode.SSE_CONNECTION_ERROR);
                            }
                        });
    }

    private SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
        emitter.onTimeout(() -> emitterRepository.deleteById(memberId));
        return emitter;
    }

    private SseEmitter.SseEventBuilder createSseEvent(
            Long memberId, NotificationType type, Object data) {
        return SseEmitter.event()
                .id(String.valueOf(memberId))
                .name(type.name())
                .data(data)
                .reconnectTime(RECONNECTION_TIME);
    }
}
