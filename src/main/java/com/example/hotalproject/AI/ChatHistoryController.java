package com.example.hotalproject.AI;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.ChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/AI")
public class ChatHistoryController {

    private final ChatMemoryRepository chatMemoryRepository;

    public ChatHistoryController(ChatMemoryRepository chatMemoryRepository) {
        this.chatMemoryRepository = chatMemoryRepository;
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatHistoryMessage>> history(
            @RequestParam String conversationId,
            Authentication authentication) {

        String userConversationId = scopedConversationId(authentication.getName(), conversationId);
        List<ChatMessage> messages = chatMemoryRepository.findByConversationId(userConversationId);

        return ResponseEntity.ok(
                messages.stream()
                        .map(message -> new ChatHistoryMessage(
                                message.getMessageType().getValue(),
                                message.getText()))
                        .toList());
    }

    private String scopedConversationId(String username, String conversationId) {
        return username + ":" + conversationId;
    }

    public record ChatHistoryMessage(String role, String content) {
    }
}
