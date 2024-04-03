package com.konggogi.veganlife.notification.controller.dto.response;


import com.konggogi.veganlife.notification.domain.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
        NotificationType type, String message, LocalDateTime createdAt) {}
