package com.konggogi.veganlife.sse.repository;


import com.konggogi.veganlife.sse.domain.Notification;
import com.konggogi.veganlife.sse.domain.NotificationType;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(
            "select n from Notification n where n.member.id = :memberId and cast(n.createdAt as localdate) = :date and n.type = :type")
    Optional<Notification> findByDateAndType(Long memberId, LocalDate date, NotificationType type);
}
