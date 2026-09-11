package com.caloryhive.business.notification.repository;

import com.caloryhive.business.notification.entity.Notification;
import com.caloryhive.business.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<Notification> findTop10ByUserOrderByCreatedAtDesc(User user);
}
