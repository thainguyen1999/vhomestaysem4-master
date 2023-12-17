package com.example.vhomestay.repository;

import com.example.vhomestay.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("select n from Notification n where n.toWhom.id = :toWhom and n.isRead = :isRead")
    List<Notification> findNotificationByToWhomIdAndIsRead(Long toWhom, boolean isRead);
}
