package com.konggogi.veganlife.notification.controller;


import com.konggogi.veganlife.global.security.user.JwtUserPrincipal;
import com.konggogi.veganlife.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/sse")
public class SseController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @AuthenticationPrincipal JwtUserPrincipal userDetails) {
        SseEmitter emitter = notificationService.subscribe(userDetails.id());
        return ResponseEntity.ok(emitter);
    }
}
