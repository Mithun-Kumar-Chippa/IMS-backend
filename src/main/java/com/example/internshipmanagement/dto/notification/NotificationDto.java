package com.example.internshipmanagement.dto.notification;

import com.example.internshipmanagement.enums.NotificationType;
import java.time.LocalDateTime;

public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean readStatus;
    private Long relatedEntityId;
    private LocalDateTime createdAt;

    public NotificationDto() {}

    public NotificationDto(Long id, String title, String message, NotificationType type, boolean readStatus, Long relatedEntityId, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.readStatus = readStatus;
        this.relatedEntityId = relatedEntityId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isReadStatus() { return readStatus; }
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }

    public Long getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
