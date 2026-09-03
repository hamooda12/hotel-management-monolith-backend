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
    CommandLineRunner checkMcpTools(ToolCallbackProvider toolCallbackProvider) {
        return args -> {
            System.out.println("========== MCP TOOLS ==========");

            for (var tool : toolCallbackProvider.getToolCallbacks()) {
                System.out.println("Tool: " + tool.getToolDefinition().name());
            }

            System.out.println("================================");
        };
    }
    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }
    @Bean
    ChatMemoryRepository chatMemoryRepository(DataSource dataSource) {
        return JdbcChatMemoryRepository.builder()
                .dialect(new PostgresChatMemoryRepositoryDialect())
                .dataSource(dataSource)
                .build();
    }

    @Bean
    ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore, ChatMemory chatMemory,
            HotelTools hotelTools,
            ToolCallbackProvider githubTools
    ) {

        return chatClientBuilder
            .defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(chatMemory).build(),
                    QuestionAnswerAdvisor.builder(vectorStore).build())
            .defaultTools(hotelTools)
            .defaultTools(githubTools)

            .build();
    }
    @Bean
    CommandLineRunner checkGitHubToken() {
        return args -> {
            String token = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN");

            System.out.println("========== GITHUB TOKEN CHECK ==========");
            System.out.println("Token exists: " + (token != null));
            System.out.println("Token length: " + (token == null ? 0 : token.length()));
            System.out.println("========================================");
        };
    }
    @Bean
    ChatClient ragChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
