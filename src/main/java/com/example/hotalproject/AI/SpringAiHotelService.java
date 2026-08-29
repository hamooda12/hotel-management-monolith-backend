package com.example.hotalproject.AI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
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
            ChatClient.Builder chatClientBuilder,
            HotelInformationService hotelInformationService, VectorStore vectorStore) {

        this.chatClient = chatClientBuilder.build();
        this.hotelInformationService = hotelInformationService;
        this.vectorStore = vectorStore;
    }

    @Value("file://${HOME}/documents/my-document.txt")
    private Resource documentResource;

    public String getRulesFor(String hotelName, String question) {

        var searchRequest = SearchRequest
                .builder()
                .query(question)
                .topK(6)
                .similarityThreshold(0.0)
                .filterExpression(
                        new FilterExpressionBuilder()
                                .eq(
                                        "hotelName",
                                        normalizeHotelName(hotelName)
                                )
                                .build()
                )
                .build();

        System.err.println(
                "Search request: " + searchRequest
        );

        List<Document> similarDocs =
                vectorStore.similaritySearch(searchRequest);

        log.info("Retrieved documents: {}", similarDocs.size());

        for (Document document : similarDocs) {
            log.info("Retrieved document text: {}", document.getText());
            log.info("Retrieved metadata: {}", document.getMetadata());
        }

        if (similarDocs.isEmpty()) {
            return "NO_HOTEL_DOCUMENTS_FOUND";
        }

        return similarDocs.stream()
                .map(Document::getText)
                .collect(
                        Collectors.joining(
                                System.lineSeparator()
                        )
                );
    }

    private String normalizeHotelName(String hotelName) {

        return hotelName
                .toLowerCase()
                .replace(" ", "_");
    }
    @Override
    public Answer askQuestion(Question question) {

        var hotelInformation =
                hotelInformationService.getInformationFor(
                        question.hotelName()
                );

        var relevantDocuments =
                getRulesFor(question.hotelName(), question.question());

        var responseEntity = chatClient.prompt()

                .system(systemSpec -> systemSpec
                        .text(systemPromptTemplate)
                        .param("hotelName", question.hotelName())
                        .param("hotelInformation", hotelInformation)
                        .param("relevantDocuments", relevantDocuments))

                .user(userSpec -> userSpec
                        .text(questionPromptTemplate)
                        .param("question", question.question()))

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