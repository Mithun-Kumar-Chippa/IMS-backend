package com.example.internshipmanagement.dto.ai;

import com.example.internshipmanagement.enums.MessageSender;
import java.time.LocalDateTime;
import java.util.List;

public class ChatResponse {
    private Long conversationId;
    private String message;
    private MessageSender sender = MessageSender.ASSISTANT;
    private List<String> retrievedSources;
    private boolean liveLlm;
    private String modelName;
    private LocalDateTime timestamp;

    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatResponse(Long conversationId, String message, List<String> retrievedSources, boolean liveLlm, String modelName) {
        this.conversationId = conversationId;
        this.message = message;
        this.sender = MessageSender.ASSISTANT;
        this.retrievedSources = retrievedSources;
        this.liveLlm = liveLlm;
        this.modelName = modelName;
        this.timestamp = LocalDateTime.now();
    }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public MessageSender getSender() { return sender; }
    public void setSender(MessageSender sender) { this.sender = sender; }

    public List<String> getRetrievedSources() { return retrievedSources; }
    public void setRetrievedSources(List<String> retrievedSources) { this.retrievedSources = retrievedSources; }

    public boolean isLiveLlm() { return liveLlm; }
    public void setLiveLlm(boolean liveLlm) { this.liveLlm = liveLlm; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
