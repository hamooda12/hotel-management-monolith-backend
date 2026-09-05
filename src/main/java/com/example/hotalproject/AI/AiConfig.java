package com.example.hotalproject.AI;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.PostgresChatMemoryRepositoryDialect;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AiConfig {

    @Bean
    CommandLineRunner checkMcpTools(ToolCallbackProvider hotelMcpTools) {
        return args -> {
            System.out.println("===================================");
            System.out.println("========== HOTEL MCP TOOLS ==========");

            for (var tool : hotelMcpTools.getToolCallbacks()) {
                System.out.println("Hotel MCP Tool: " + tool.getToolDefinition().name());
            }

            System.out.println("======================================");
        };
    }

    @Bean
    ChatMemoryRepository chatMemoryRepository(DataSource dataSource) {
        return JdbcChatMemoryRepository.builder()
                .dialect(new PostgresChatMemoryRepositoryDialect())
                .dataSource(dataSource)
                .build();
    }

    @Bean
    ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }

    @Bean
    ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            ChatMemory chatMemory,
            ToolCallbackProvider hotelMcpTools
    ) {
        return chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build())
                .defaultTools(hotelMcpTools)
                .build();
    }

    @Bean
    ChatClient ragChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
