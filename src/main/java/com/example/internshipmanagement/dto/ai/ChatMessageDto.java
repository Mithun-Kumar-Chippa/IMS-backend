package com.example.internshipmanagement.dto.ai;

import com.example.internshipmanagement.enums.MessageSender;
import java.time.LocalDateTime;

public class ChatMessageDto {
    private Long id;
    private Long conversationId;
    private MessageSender sender;
    private String content;
    private String retrievedContext;
    private LocalDateTime createdAt;

    public ChatMessageDto() {}

    public ChatMessageDto(Long id, Long conversationId, MessageSender sender, String content, String retrievedContext, LocalDateTime createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.sender = sender;
        this.content = content;
        this.retrievedContext = retrievedContext;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public MessageSender getSender() { return sender; }
    public void setSender(MessageSender sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRetrievedContext() { return retrievedContext; }
    public void setRetrievedContext(String retrievedContext) { this.retrievedContext = retrievedContext; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
