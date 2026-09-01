package com.example.hotalproject.AI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.metadata.Usage;

import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpringAiHotelService implements HotelAIService {

    private final ChatClient chatClient;
    private final HotelInformationService hotelInformationService;
    private final VectorStore vectorStore;
    @Value("classpath:/promptTemplates/questionPromptTemplate.st")
    private Resource questionPromptTemplate;

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    private Resource systemPromptTemplate;

    public SpringAiHotelService(
          ChatClient chatClient,
            HotelInformationService hotelInformationService, VectorStore vectorStore) {

        this.chatClient = chatClient;
        this.hotelInformationService = hotelInformationService;
        this.vectorStore = vectorStore;
    }


    @Override
    public Answer askQuestion(Question question) {

        var hotelInformation =
                hotelInformationService.getInformationFor(
                        question.hotelName()
                );


        log.info("Hotel Information:\n{}", hotelInformation);

        log.info("Question:\n{}", question.question());

        var hotelMatch = String.format(
                "hotelName == '%s'",
                question.hotelName());

        var advisor = RetrievalAugmentationAdvisor.builder()

                .queryTransformers(
                        TranslationQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate())
                                .targetLanguage("English")
                                .build(),

                        RewriteQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate())
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

        var responseEntity = chatClient.prompt()

                .system(systemSpec -> systemSpec
                        .text(systemPromptTemplate)
                        .param("hotelName", question.hotelName())
                        .param("hotelInformation", hotelInformation)
                )

                .user(userSpec -> userSpec
                        .text(questionPromptTemplate)
                        .param("question", question.question()))
                .advisors(advisor)

                .call()

                .responseEntity(Answer.class);

        var response = responseEntity.response();

        assert response != null;

        var metadata = response.getMetadata();

        logUsage(metadata.getUsage());

        return responseEntity.entity();
    }
    private void logUsage(Usage usage) {

        log.info(
                "Token usage: prompt={}, generation={}, total={}",
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens()
        );

        log.info(
                "Native Gemini usage: {}",
                usage.getNativeUsage()
        );
    }
}