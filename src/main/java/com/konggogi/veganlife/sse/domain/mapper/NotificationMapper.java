package com.konggogi.veganlife.sse.domain.mapper;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.sse.domain.Notification;
import com.konggogi.veganlife.sse.domain.NotificationType;
import com.konggogi.veganlife.sse.service.dto.NotificationData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "id", ignore = true)
    Notification toEntity(Member member, NotificationType type, String message);

    NotificationData toNotificationData(Notification notification);
}
