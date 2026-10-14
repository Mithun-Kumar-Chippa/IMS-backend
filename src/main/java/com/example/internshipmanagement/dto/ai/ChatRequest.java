package com.example.internshipmanagement.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatRequest {

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 2000, message = "Message exceeds maximum allowed length of 2000 characters")
    private String message;

    private Long conversationId;

    public ChatRequest() {}

    public ChatRequest(String message, Long conversationId) {
        this.message = message;
        this.conversationId = conversationId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
}
