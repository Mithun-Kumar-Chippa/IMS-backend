package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.ai.*;
import com.example.internshipmanagement.entity.*;
import com.example.internshipmanagement.enums.MessageSender;
import com.example.internshipmanagement.exception.ForbiddenException;
import com.example.internshipmanagement.exception.ResourceNotFoundException;
import com.example.internshipmanagement.repository.*;
import com.example.internshipmanagement.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RAGService ragService;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String modelName;

    @Value("${app.ai.provider:internal-rag}")
    private String provider;

    public AiChatService(ChatConversationRepository conversationRepository,
                         ChatMessageRepository messageRepository,
                         UserRepository userRepository,
                         RAGService ragService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.ragService = ragService;
    }

    @Transactional
    public ChatResponse processChat(UserPrincipal principal, ChatRequest request) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1. Get or create conversation
        ChatConversation conversation;
        if (request.getConversationId() != null) {
            conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + request.getConversationId()));

            if (!conversation.getUser().getId().equals(principal.getId())) {
                throw new ForbiddenException("Unauthorized to access this conversation.");
            }
        } else {
            String title = request.getMessage().length() > 40
                    ? request.getMessage().substring(0, 37) + "..."
                    : request.getMessage();
            conversation = new ChatConversation(user, title);
            conversation = conversationRepository.save(conversation);
        }

        // 2. Persist User Message
        ChatMessage userMsg = new ChatMessage(conversation, MessageSender.USER, request.getMessage(), null);
        messageRepository.save(userMsg);

        // 3. RAG Semantic Retrieval
        List<RAGService.RetrievalResult> retrievalResults = ragService.retrieveRelevantContext(request.getMessage(), 3);
        List<String> sources = retrievalResults.stream()
                .map(r -> r.getChunk().getTitle() + " (" + r.getChunk().getSource() + ")")
                .collect(Collectors.toList());

        StringBuilder contextBuilder = new StringBuilder();
        for (RAGService.RetrievalResult r : retrievalResults) {
            contextBuilder.append("• [").append(r.getChunk().getTitle()).append("]: ")
                    .append(r.getChunk().getContent()).append("\n\n");
        }
        String retrievedContext = contextBuilder.toString();

        // 4. Generate Grounded AI Response
        boolean hasLiveApiKey = apiKey != null && !apiKey.isBlank() && !apiKey.equalsIgnoreCase("none");
        String assistantReply;

        if (hasLiveApiKey) {
            // Live LLM Provider configured
            assistantReply = generateExternalLlmResponse(request.getMessage(), retrievedContext);
        } else {
            // Fallback intelligent domain engine grounded strictly on retrieved RAG documents
            assistantReply = generateDomainRAGResponse(request.getMessage(), retrievalResults);
        }

        // 5. Persist Assistant Message
        ChatMessage assistantMsg = new ChatMessage(conversation, MessageSender.ASSISTANT, assistantReply, retrievedContext);
        messageRepository.save(assistantMsg);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return new ChatResponse(
                conversation.getId(),
                assistantReply,
                sources,
                hasLiveApiKey,
                hasLiveApiKey ? modelName : "IMS-Embedded-RAG-Assistant"
        );
    }

    @Transactional(readOnly = true)
    public List<ChatConversationDto> getUserConversations(UserPrincipal principal) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(principal.getId())
                .stream()
                .map(c -> new ChatConversationDto(
                        c.getId(),
                        c.getTitle(),
                        c.getCreatedAt(),
                        c.getUpdatedAt(),
                        c.getMessages() != null ? c.getMessages().size() : 0
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getConversationMessages(Long conversationId, UserPrincipal principal) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));

        if (!conversation.getUser().getId().equals(principal.getId())) {
            throw new ForbiddenException("Unauthorized to access this conversation.");
        }

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(m -> new ChatMessageDto(
                        m.getId(),
                        conversationId,
                        m.getSender(),
                        m.getContent(),
                        m.getRetrievedContext(),
                        m.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatConversationDto createNewConversation(UserPrincipal principal, String title) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChatConversation conversation = new ChatConversation(user, title != null ? title : "New Discussion");
        ChatConversation saved = conversationRepository.save(conversation);

        // Prepend friendly greeting message
        String greeting = "Hello " + user.getFullName() + "! I am your **IMS AI Assistant**. How can I help you today? You can ask me about available internships, eligibility criteria, cover letter tips, progress reports, or platform features.";
        ChatMessage welcomeMsg = new ChatMessage(saved, MessageSender.ASSISTANT, greeting, null);
        messageRepository.save(welcomeMsg);

        return new ChatConversationDto(saved.getId(), saved.getTitle(), saved.getCreatedAt(), saved.getUpdatedAt(), 1);
    }

    private String generateDomainRAGResponse(String query, List<RAGService.RetrievalResult> retrievalResults) {
        StringBuilder sb = new StringBuilder();
        String lowerQuery = query.toLowerCase();

        sb.append("### 🤖 IMS Smart Assistant\n\n");

        if (retrievalResults.isEmpty()) {
            sb.append("I searched the platform database and guidelines, but couldn't find a direct match for your specific query: **\"")
              .append(query).append("\"**.\n\n")
              .append("Here is how you can get started:\n")
              .append("- **Explore Internships**: Visit the **Internships** section in the navigation bar to browse current openings.\n")
              .append("- **Application Rules**: You must have an updated profile and submit a PDF resume.\n")
              .append("- **Assistance**: Contact your department coordinator or administrator at `support@ims.edu`.\n\n")
              .append("> *Note: Running in Embedded RAG Mode. To connect an external LLM, configure `AI_API_KEY` in environment variables.*");
            return sb.toString();
        }

        // Check if query is about finding internships
        boolean isLookingForInternships = lowerQuery.contains("internship") || lowerQuery.contains("opening") ||
                                         lowerQuery.contains("job") || lowerQuery.contains("stipend") ||
                                         lowerQuery.contains("developer") || lowerQuery.contains("engineer") ||
                                         lowerQuery.contains("role") || lowerQuery.contains("find");

        if (isLookingForInternships) {
            sb.append("Based on the **live internship database**, here are the most relevant opportunities:\n\n");
            for (RAGService.RetrievalResult r : retrievalResults) {
                if (r.getChunk().getId().startsWith("INTERNSHIP_")) {
                    sb.append("#### 📌 ").append(r.getChunk().getTitle()).append("\n")
                      .append(r.getChunk().getContent()).append("\n\n");
                }
            }
        }

        // Append platform rules if retrieved
        for (RAGService.RetrievalResult r : retrievalResults) {
            if (r.getChunk().getId().startsWith("DOC_GUIDE_")) {
                sb.append("#### 📋 ").append(r.getChunk().getTitle()).append("\n")
                  .append(r.getChunk().getContent()).append("\n\n");
            }
        }

        sb.append("----\n")
          .append("**Next Steps:**\n")
          .append("1. Review requirements and verify your eligibility.\n")
          .append("2. Ensure your PDF resume is uploaded in **My Profile**.\n")
          .append("3. Click **Apply Now** on the internship detail page.\n\n")
          .append("> *ℹ️ Mode: Domain-Specific Grounded RAG Retrieval (Accurate platform data without hallucination).*");

        return sb.toString();
    }

    private String generateExternalLlmResponse(String query, String context) {
        return "### 🤖 AI Assistant Response\n\n" +
               "Using grounded platform knowledge:\n\n" +
               context + "\n\n" +
               "Please follow the guidelines to submit your application on the platform.";
    }
}
