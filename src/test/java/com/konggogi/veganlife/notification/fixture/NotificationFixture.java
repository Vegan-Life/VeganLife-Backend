package com.konggogi.veganlife.notification.fixture;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.notification.domain.Notification;
import com.konggogi.veganlife.notification.domain.NotificationMessage;
import com.konggogi.veganlife.notification.domain.NotificationType;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.springframework.util.ReflectionUtils;

public enum NotificationFixture {
    SSE(NotificationType.SSE, NotificationMessage.SSE_CONNECTION.getMessage()),
    INTAKE_OVER_30(
            NotificationType.INTAKE_OVER_30, NotificationMessage.OVER_INTAKE.getMessage(100)),
    INTAKE_OVER_60(
            NotificationType.INTAKE_OVER_60, NotificationMessage.OVER_INTAKE.getMessage(100)),
    MENTION(NotificationType.MENTION, NotificationMessage.MENTION.getMessage("test1", "test2")),
    COMMENT_LIKE(
            NotificationType.COMMENT,
            NotificationMessage.COMMENT_LIKE.getMessage("test1", "test2"));

    private final NotificationType type;
    private final String message;

    NotificationFixture(NotificationType type, String message) {
        this.type = type;
        this.message = message;
    }

    public Notification get(Member member) {
        return Notification.builder().type(type).message(message).member(member).build();
    }

    public Notification getWithId(Long id, Member member) {
        return Notification.builder().id(id).type(type).message(message).member(member).build();
    }

    public Notification getWithMessage(Member member, String message) {
        return Notification.builder().type(type).message(message).member(member).build();
    }

    public Notification getWithDate(Member member, LocalDateTime createdAt) {
        Notification notification =
                Notification.builder().type(type).message(message).member(member).build();
        return setCreatedAt(notification, createdAt);
    }

    private Notification setCreatedAt(Notification notification, LocalDateTime createdAt) {
        Field createdAtField = ReflectionUtils.findField(Notification.class, "createdAt");
        ReflectionUtils.makeAccessible(createdAtField);
        ReflectionUtils.setField(createdAtField, notification, createdAt);
        return notification;
    }
}
