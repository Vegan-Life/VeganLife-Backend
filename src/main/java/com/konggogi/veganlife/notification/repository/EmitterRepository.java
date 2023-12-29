package com.konggogi.veganlife.notification.repository;


import java.util.Optional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
    void save(Long memberId, SseEmitter emitter);

    void deleteById(Long memberId);

    Optional<SseEmitter> findById(Long memberId);
}
