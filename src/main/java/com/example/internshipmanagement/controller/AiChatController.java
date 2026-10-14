package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.ai.*;
import com.example.internshipmanagement.dto.common.ApiResponse;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Chatbot & RAG", description = "Endpoints for AI-powered internship assistant, RAG query processing, and conversation persistence")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Send a prompt to the RAG AI assistant and receive grounded answer")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChatRequest request) {
        ChatResponse response = aiChatService.processChat(principal, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/conversations")
    @Operation(summary = "Get current user's past chat conversations")
    public ResponseEntity<ApiResponse<List<ChatConversationDto>>> getConversations(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ChatConversationDto> conversations = aiChatService.getUserConversations(principal);
        return ResponseEntity.ok(ApiResponse.success(conversations));
    }

    @PostMapping("/conversations")
    @Operation(summary = "Start a fresh chat conversation")
    public ResponseEntity<ApiResponse<ChatConversationDto>> createConversation(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false, defaultValue = "New Conversation") String title) {
        ChatConversationDto conv = aiChatService.createNewConversation(principal, title);
        return ResponseEntity.ok(ApiResponse.success(conv));
    }

    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "Get full message transcript for a specific conversation")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getMessages(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ChatMessageDto> messages = aiChatService.getConversationMessages(id, principal);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
}
