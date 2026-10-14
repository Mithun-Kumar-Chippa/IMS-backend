package com.example.internshipmanagement.entity;

import com.example.internshipmanagement.enums.MessageSender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_msg_conv", columnList = "conversation_id")
})
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    @JsonIgnore
    private ChatConversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageSender sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String retrievedContext;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public ChatMessage() {}

    public ChatMessage(ChatConversation conversation, MessageSender sender, String content, String retrievedContext) {
        this.conversation = conversation;
        this.sender = sender;
        this.content = content;
        this.retrievedContext = retrievedContext;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChatConversation getConversation() { return conversation; }
    public void setConversation(ChatConversation conversation) { this.conversation = conversation; }

    public MessageSender getSender() { return sender; }
    public void setSender(MessageSender sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRetrievedContext() { return retrievedContext; }
    public void setRetrievedContext(String retrievedContext) { this.retrievedContext = retrievedContext; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
