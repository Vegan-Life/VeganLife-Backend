package com.konggogi.veganlife.notification.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.notification.controller.dto.response.NotificationResponse;
import com.konggogi.veganlife.notification.domain.mapper.NotificationMapper;
import com.konggogi.veganlife.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
public class SseController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SseEmitter emitter = notificationService.subscribe(userDetails.id());
        return ResponseEntity.ok(emitter);
    }

    @GetMapping("/notifications")
    public ResponseEntity<Page<NotificationResponse>> getNotificationList(
            Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(
                notificationService
                        .searchByMember(userDetails.id(), pageable)
                        .map(notificationMapper::toNotificationResponse));
    }
}
