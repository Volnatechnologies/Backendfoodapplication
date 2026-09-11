package com.caloryhive.business.notification.service;

import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.notification.entity.Notification;
import com.caloryhive.business.notification.repository.NotificationRepository;
import com.caloryhive.business.user.entity.User;
import com.caloryhive.business.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Page<Notification> getNotifications(Pageable pageable) {
        User user = getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    public Notification markRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllRead() {
        User user = getCurrentUser();
        notificationRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged())
                .forEach(notification -> notification.setRead(true));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
