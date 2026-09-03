package com.example.hotalproject.AI;

import com.example.hotalproject.HotelCatalog.availability.AvailabilityCheckRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/hotelAI/")
public class HotelAiController {

    private static final String ROOM_AVAILABILITY_TEMPLATE =
            "Is room {roomId} available on {date}?";

    private final ChatClient chatClient;


    public HotelAiController(ChatClient.Builder chatClientBuilder,
                             HotelTools hotelTools) {
        this.chatClient = chatClientBuilder
                .defaultTools(hotelTools)
                .build();

    }

    @GetMapping("/room/availability")
    public String checkAvailability(
           @RequestParam @NotNull @Min(1) Long roomId,
           @RequestParam @NotNull LocalDate checkinDate){

        return chatClient.prompt()
                .user(userSpec -> {
                    userSpec
                            .text(ROOM_AVAILABILITY_TEMPLATE).param("roomId", roomId)
                            .param("date", checkinDate.toString());

                })
                .call()
                .content();
    }
}