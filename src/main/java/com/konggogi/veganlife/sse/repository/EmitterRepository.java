package com.konggogi.veganlife.sse.repository;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void save(Long memberId, SseEmitter emitter) {
        emitters.put(memberId, emitter);
    }

    public void deleteById(Long memberId) {
        emitters.remove(memberId);
    }

    public Optional<SseEmitter> findById(Long memberId) {
        return Optional.ofNullable(emitters.get(memberId));
    }
}
