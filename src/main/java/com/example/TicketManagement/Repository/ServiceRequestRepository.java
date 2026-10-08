package com.example.TicketManagement.Repository;

import com.example.TicketManagement.Entity.ServiceRequest;
import com.example.TicketManagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    // Fetch only tickets belonging to the logged-in user
    List<ServiceRequest> findByUser(User user);

    // Fetch a specific ticket only if it belongs to the given user
    Optional<ServiceRequest> findByTicketIdAndUser(Long ticketId, User user);
}