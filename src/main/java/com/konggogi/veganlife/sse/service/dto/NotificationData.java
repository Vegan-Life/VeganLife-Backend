package com.konggogi.veganlife.sse.service.dto;


import com.konggogi.veganlife.sse.domain.NotificationType;

public record NotificationData(NotificationType type, String message) {}
