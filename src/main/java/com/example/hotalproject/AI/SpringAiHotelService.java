package com.example.hotalproject.AI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
public class SpringAiHotelService implements HotelAIService {

    private final ChatClient chatClient;
    private final HotelInformationService hotelInformationService;
    private final VectorStore vectorStore;
    private final ChatClient ragChatClient;

    @Value("classpath:/promptTemplates/questionPromptTemplate.st")
    private Resource questionPromptTemplate;

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    private Resource systemPromptTemplate;

    public SpringAiHotelService(ChatClient chatClient, HotelInformationService hotelInformationService, VectorStore vectorStore, ChatClient ragChatClient) {
        this.chatClient = chatClient;
        this.hotelInformationService = hotelInformationService;
        this.vectorStore = vectorStore;
        this.ragChatClient = ragChatClient;
    }

    @Override
    public Answer askQuestion(Question question) {
        var hotelInformation = hotelInformationService.getInformationFor(question.hotelName());
        var advisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(
                        TranslationQueryTransformer.builder().chatClientBuilder(ragChatClient.mutate()).targetLanguage("English").build(),
                        RewriteQueryTransformer.builder().chatClientBuilder(ragChatClient.mutate()).build())
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore).similarityThreshold(0.0).topK(6).build())
                .build();

        var responseEntity = chatClient.prompt()
                .system(systemSpec -> systemSpec.text(systemPromptTemplate)
                        .param("hotelName", question.hotelName())
                        .param("hotelInformation", hotelInformation)
                        .param("conversationId", question.conversationId()))
                .user(userSpec -> userSpec.text(questionPromptTemplate).param("question", question.question()))
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, scopedConversationId(question.conversationId())))
                .advisors(advisor)
                .call()
                .responseEntity(Answer.class);

        var response = responseEntity.response();
        assert response != null;
        logUsage(response.getMetadata().getUsage());
        return responseEntity.entity();
    }

    @Override
    public Answer askNormalQuestion(Question question) {
        var responseEntity = chatClient.prompt()
                .system(systemSpec -> systemSpec.text(systemPromptTemplate)
                        .param("hotelName", question.hotelName() == null ? "" : question.hotelName())
                        .param("hotelInformation", "")
                        .param("conversationId", question.conversationId()))
                .user(userSpec -> userSpec.text(question.question()))
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, scopedConversationId(question.conversationId())))
                .call()
                .responseEntity(Answer.class);

        var response = responseEntity.response();
        assert response != null;
        logUsage(response.getMetadata().getUsage());
        return responseEntity.entity();
    }

    private String scopedConversationId(String conversationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalStateException("Authenticated user is required for AI conversation memory");
        }

        // Spring AI's JDBC chat-memory schema uses VARCHAR(36) for conversation_id.
        // Hash the authenticated user + client conversation ID into a deterministic UUID
        // so the value is exactly 36 characters while remaining user-scoped.
        String scope = authentication.getName() + ":" + conversationId;
        return UUID.nameUUIDFromBytes(scope.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private void logUsage(Usage usage) {
        log.info("Token usage: prompt={}, generation={}, total={}", usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        log.info("Native Gemini usage: {}", usage.getNativeUsage());
    }
}
