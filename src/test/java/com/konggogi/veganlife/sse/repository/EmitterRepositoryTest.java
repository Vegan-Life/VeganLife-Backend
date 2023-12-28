package com.konggogi.veganlife.sse.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class EmitterRepositoryTest {
    private EmitterRepository emitterRepository;
    private final SseEmitter emitter = new SseEmitter();
    private final Long memberId = 1L;

    @BeforeEach
    void setup() {
        emitterRepository = new EmitterRepositoryImpl();
    }

    @Test
    @DisplayName("SseEmitter 저장")
    void saveTest() {
        // when, then
        assertThatNoException().isThrownBy(() -> emitterRepository.save(memberId, emitter));
    }

    @Test
    @DisplayName("회원 ID로 SseEmitter 조회")
    void findByIdTest() {
        // given
        emitterRepository.save(memberId, emitter);
        // when
        Optional<SseEmitter> foundEmitter = emitterRepository.findById(memberId);
        // then
        assertThat(foundEmitter).isPresent();
    }

    @Test
    @DisplayName("회원 ID로 SseEmitter 삭제")
    void deleteByIdTest() {
        // given
        emitterRepository.save(memberId, emitter);
        // when, then
        assertThatNoException().isThrownBy(() -> emitterRepository.deleteById(memberId));
    }
}
