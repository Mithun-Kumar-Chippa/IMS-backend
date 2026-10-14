package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.common.PagedResponse;
import com.example.internshipmanagement.dto.notification.NotificationDto;
import com.example.internshipmanagement.entity.Notification;
import com.example.internshipmanagement.entity.User;
import com.example.internshipmanagement.enums.NotificationType;
import com.example.internshipmanagement.exception.ResourceNotFoundException;
import com.example.internshipmanagement.repository.NotificationRepository;
import com.example.internshipmanagement.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Async
    public void sendNotification(User recipient, String title, String message, NotificationType type, Long relatedEntityId) {
        Notification notification = new Notification(recipient, title, message, type, relatedEntityId);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public PagedResponse<NotificationDto> getUserNotifications(UserPrincipal principal, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(principal.getId(), pageable);
        List<NotificationDto> dtos = page.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return new PagedResponse<>(dtos, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getRecentNotifications(UserPrincipal principal) {
        return notificationRepository.findTop20ByRecipientIdOrderByCreatedAtDesc(principal.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UserPrincipal principal) {
        return notificationRepository.countByRecipientIdAndReadStatusFalse(principal.getId());
    }

    @Transactional
    public void markAsRead(Long id, UserPrincipal principal) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        if (!notification.getRecipient().getId().equals(principal.getId())) {
            throw new ResourceNotFoundException("Notification not found with id: " + id);
        }

        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(UserPrincipal principal) {
        List<Notification> unread = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(principal.getId(), Pageable.unpaged()).getContent();
        for (Notification n : unread) {
            n.setReadStatus(true);
        }
        notificationRepository.saveAll(unread);
    }

    private NotificationDto mapToDto(Notification n) {
        return new NotificationDto(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.isReadStatus(),
                n.getRelatedEntityId(),
                n.getCreatedAt()
        );
    }
}
