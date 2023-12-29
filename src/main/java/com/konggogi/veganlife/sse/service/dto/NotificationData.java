package com.konggogi.veganlife.sse.service.dto;


import com.konggogi.veganlife.sse.domain.NotificationType;
import java.time.LocalDateTime;

public record NotificationData(NotificationType type, String message, LocalDateTime createdAt) {}
