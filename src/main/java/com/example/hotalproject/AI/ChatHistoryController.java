package com.example.hotalproject.AI;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

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

        String scopedId = scopedConversationId(authentication.getName(), conversationId);
        List<Message> messages = chatMemoryRepository.findByConversationId(scopedId);

        return ResponseEntity.ok(
                messages.stream()
                        .map(message -> new ChatHistoryMessage(
                                message.getMessageType().getValue(),
                                message.getText()))
                        .toList());
    }

    private String scopedConversationId(String username, String conversationId) {
        String scope = username + ":" + conversationId;
        return UUID.nameUUIDFromBytes(scope.getBytes(StandardCharsets.UTF_8)).toString();
    }

    public record ChatHistoryMessage(String role, String content) {
    }
}
