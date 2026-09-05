package com.example.hotalproject.AI;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/AI")
public class AskController {

    private final HotelAIService hotelAIService;

    public AskController(HotelAIService hotelAIService) {
        this.hotelAIService = hotelAIService;
    }

    @PostMapping("/ask")
    public Answer ask(
            @RequestBody @Valid Question question) {

        return hotelAIService.askQuestion(question);
    }
    @PostMapping("/ask/normal")
    public Answer askNormal(
            @RequestBody @Valid String question) {

        return hotelAIService.askNormalQuestion(question);
    }
}