package com.konggogi.veganlife.sse.repository;


import com.konggogi.veganlife.sse.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {}
