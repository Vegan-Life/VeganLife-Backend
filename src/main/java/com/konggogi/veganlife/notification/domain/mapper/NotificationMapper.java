package com.konggogi.veganlife.notification.domain.mapper;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.notification.domain.Notification;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.service.dto.NotificationData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "id", ignore = true)
    Notification toEntity(Member member, NotificationType type, String message);

    NotificationData toNotificationData(Notification notification);
}
