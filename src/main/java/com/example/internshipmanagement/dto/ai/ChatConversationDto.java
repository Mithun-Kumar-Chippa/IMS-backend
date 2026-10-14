package com.example.internshipmanagement.dto.ai;

import java.time.LocalDateTime;

public class ChatConversationDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int messageCount;

    public ChatConversationDto() {}

    public ChatConversationDto(Long id, String title, LocalDateTime createdAt, LocalDateTime updatedAt, int messageCount) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messageCount = messageCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getMessageCount() { return messageCount; }
    public void setMessageCount(int messageCount) { this.messageCount = messageCount; }
}
