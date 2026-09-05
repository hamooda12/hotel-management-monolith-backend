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
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

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

    public SpringAiHotelService(
            ChatClient chatClient,
            HotelInformationService hotelInformationService,
            VectorStore vectorStore,
            ChatClient ragChatClient) {
        this.chatClient = chatClient;
        this.hotelInformationService = hotelInformationService;
        this.vectorStore = vectorStore;
        this.ragChatClient = ragChatClient;
    }

    @Override
    public Answer askQuestion(Question question) {
        String hotelInformation = hotelInformationService.getInformationFor(question.hotelName());

        log.info("Question: {}", question.question());
        log.info("hotelName = {}", question.hotelName());
        log.info("conversationId = {}", question.conversationId());

        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(
                        TranslationQueryTransformer.builder()
                                .chatClientBuilder(ragChatClient.mutate())
                                .targetLanguage("English")
                                .build(),
                        RewriteQueryTransformer.builder()
                                .chatClientBuilder(ragChatClient.mutate())
                                .build()
                )
                .documentRetriever(
                        VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .similarityThreshold(0.0)
                                .topK(6)
                                .build()
                )
                .build();

        logQdrantResults(question.question());

        var responseEntity = chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(systemPromptTemplate)
                        .param("hotelName", question.hotelName())
                        .param("hotelInformation", hotelInformation))
                .user(userSpec -> userSpec
                        .text(questionPromptTemplate)
                        .param("question", question.question()))
                .advisors(advisorSpec -> advisorSpec
                        .param(ChatMemory.CONVERSATION_ID, question.conversationId()))
                .advisors(ragAdvisor)
                .call()
                .responseEntity(Answer.class);

        logUsage(responseEntity.response().getMetadata().getUsage());
        return responseEntity.entity();
    }

    private void logQdrantResults(String question) {
        var testResults = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(6)
                        .similarityThreshold(0.0)
                        .build());

        log.info("========== QDRANT TEST ==========");
        for (var doc : testResults) {
            log.info("CONTENT: {}", doc.getText());
            log.info("METADATA: {}", doc.getMetadata());
        }
        log.info("=================================");
    }

    private void logUsage(Usage usage) {
        if (usage == null) {
            return;
        }

        log.info(
                "Token usage: prompt={}, generation={}, total={}",
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens());

        log.info("Native usage: {}", usage.getNativeUsage());
    }

    @Override
    public Answer askNormalQuestion(Question question) {
        var responseEntity = chatClient.prompt()
                .user(question.question())
                .advisors(advisorSpec -> advisorSpec
                        .param(ChatMemory.CONVERSATION_ID, question.conversationId()))
                .call()
                .responseEntity(Answer.class);

        logUsage(responseEntity.response().getMetadata().getUsage());
        return responseEntity.entity();
    }
}
