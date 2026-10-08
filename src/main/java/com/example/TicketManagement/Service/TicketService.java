package com.example.TicketManagement.Service;

import com.example.TicketManagement.Entity.ServiceRequest;
import com.example.TicketManagement.Entity.User;
import com.example.TicketManagement.Repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final ServiceRequestRepository repository;

    public TicketService(ServiceRequestRepository repository) {
        this.repository = repository;
    }

    public ServiceRequest createTicket(ServiceRequest request) {
        return repository.save(request);
    }

    public List<ServiceRequest> fetchTickets() {
        updateTicketStatuses();
        return repository.findAll();
    }

    public Optional<ServiceRequest> findTicketById(Long id) {
        updateTicketStatuses();
        return repository.findById(id);
    }

    // Fetch only tickets belonging to a specific user
    public List<ServiceRequest> fetchTicketsByUser(User user) {

        updateTicketStatuses();

        return repository.findByUser(user);
    }

    // Fetch a ticket only when it belongs to the logged-in user
    public Optional<ServiceRequest> findTicketByIdAndUser(Long id, User user) {

        updateTicketStatuses();

        return repository.findByTicketIdAndUser(id, user);
    }

    public ServiceRequest updateTicket(ServiceRequest request) {
        return repository.save(request);
    }

    public void deleteTicket(Long id) {
        repository.deleteById(id);
    }

    public void updateTicketStatuses() {

        List<ServiceRequest> tickets = repository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (ServiceRequest ticket : tickets) {

            if (ticket.getCreatedAt() == null) {
                continue;
            }

            long minutesElapsed = Duration.between(
                    ticket.getCreatedAt(),
                    now
            ).toMinutes();

            if (minutesElapsed >= 255) {

                ticket.setStatus("Closed");
                ticket.setAssignedTo("IT Support Team");
                ticket.setResolutionDetails(
                        "Service request resolved and closed by the support team."
                );

            } else if (minutesElapsed >= 240) {

                ticket.setStatus("Resolved");
                ticket.setAssignedTo("IT Support Team");
                ticket.setResolutionDetails(
                        "Issue resolved by the support team."
                );

            } else if (minutesElapsed >= 60) {

                ticket.setStatus("In Progress");
                ticket.setAssignedTo("IT Support Team");

            } else {

                ticket.setStatus("Open");
                ticket.setAssignedTo(null);
                ticket.setResolutionDetails(null);
            }

            repository.save(ticket);
        }
    }
}