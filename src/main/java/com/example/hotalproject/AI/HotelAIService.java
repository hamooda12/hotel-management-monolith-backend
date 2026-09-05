package com.example.hotalproject.AI;


import reactor.core.publisher.Flux;

public interface HotelAIService {
   Answer askQuestion(Question question);
   Answer askNormalQuestion(Question question);
}