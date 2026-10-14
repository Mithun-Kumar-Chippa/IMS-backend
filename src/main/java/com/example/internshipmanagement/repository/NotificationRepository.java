package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);
    List<Notification> findTop20ByRecipientIdOrderByCreatedAtDesc(Long recipientId);
    long countByRecipientIdAndReadStatusFalse(Long recipientId);
}
