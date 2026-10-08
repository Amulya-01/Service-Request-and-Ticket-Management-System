package com.example.TicketManagement.Controller;

import com.example.TicketManagement.Service.AIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/ai/ask")
    public String ask(
            @RequestParam String question) {

        return aiService.askQuestion(question);
    }
}