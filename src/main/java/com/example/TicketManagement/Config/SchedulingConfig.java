package com.example.TicketManagement.Config;

import com.example.TicketManagement.Service.TicketService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private final TicketService ticketService;

    public SchedulingConfig(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Scheduled(fixedRate = 60000)
    public void updateTicketStatuses() {
        ticketService.updateTicketStatuses();
    }
}
