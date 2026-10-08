package com.example.TicketManagement.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AIPageController {

    @GetMapping("/ai-assistant")
    public String aiAssistant() {
        return "ai-assistant";
    }
}