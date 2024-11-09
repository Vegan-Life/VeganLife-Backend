package com.konggogi.veganlife.notification.service.dto;


import com.konggogi.veganlife.notification.domain.NotificationType;
import java.time.LocalDateTime;

public record NotificationData(
        Long id, NotificationType type, String message, LocalDateTime createdAt) {}
