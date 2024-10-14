package com.konggogi.veganlife.notification.repository;


import com.konggogi.veganlife.notification.domain.Notification;
import com.konggogi.veganlife.notification.domain.NotificationType;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(
            "select n from Notification n where n.member.id = :memberId and cast(n.createdAt as localdate) = :date and n.type = :type")
    Optional<Notification> findByDateAndType(Long memberId, LocalDate date, NotificationType type);

    void deleteAllByMemberId(Long memberId);

    @Query(
            "select n from Notification n where n.member.id = :memberId and n.type != 'SSE' order by n.createdAt desc")
    Page<Notification> findAllByMember(Long memberId, Pageable pageable);
}
