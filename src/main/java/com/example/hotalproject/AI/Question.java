package com.example.hotalproject.AI;

import jakarta.validation.constraints.NotBlank;

public record Question(


        String hotelName,

        @NotBlank(message = "Question is required")
        String question,

        @NotBlank(message = "Conversation ID is required")
        String conversationId
) {
}