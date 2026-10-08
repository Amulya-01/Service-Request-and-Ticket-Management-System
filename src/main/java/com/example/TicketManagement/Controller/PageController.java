package com.example.TicketManagement.Controller;

import com.example.TicketManagement.Entity.ServiceRequest;
import com.example.TicketManagement.Entity.User;
import com.example.TicketManagement.Service.TicketService;
import com.example.TicketManagement.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class PageController {

    private final UserService userService;
    private final TicketService ticketService;

    public PageController(UserService userService,
                          TicketService ticketService) {
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user) {

        userService.saveUser(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ---------------------------------------------------------
    // DASHBOARD
    // ---------------------------------------------------------

    @GetMapping("/dashboard")
    public String dashboardPage(
            Model model,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        List<ServiceRequest> tickets =
                ticketService.fetchTicketsByUser(loggedInUser);

        long total = tickets.size();

        long open = tickets.stream()
                .filter(t -> "Open".equalsIgnoreCase(t.getStatus()))
                .count();

        long inProgress = tickets.stream()
                .filter(t -> "In Progress".equalsIgnoreCase(t.getStatus()))
                .count();

        long resolved = tickets.stream()
                .filter(t -> "Resolved".equalsIgnoreCase(t.getStatus())
                        || "Closed".equalsIgnoreCase(t.getStatus()))
                .count();

        model.addAttribute("totalTickets", total);
        model.addAttribute("openTickets", open);
        model.addAttribute("inProgressTickets", inProgress);
        model.addAttribute("resolvedTickets", resolved);

        return "dashboard";
    }

    // ---------------------------------------------------------
    // CREATE TICKET
    // ---------------------------------------------------------

    @GetMapping("/create-ticket")
    public String createTicketPage(Model model) {

        model.addAttribute("ticket", new ServiceRequest());

        return "create-ticket";
    }

    @PostMapping("/create-ticket")
    public String createTicket(
            @ModelAttribute("ticket") ServiceRequest ticket,
            @RequestParam(value = "screenshot", required = false)
            MultipartFile screenshot,
            Authentication authentication) {

        if (ticket.getIssue() == null
                || ticket.getIssue().isBlank()
                || ticket.getIssueDescription() == null
                || ticket.getIssueDescription().isBlank()
                || ticket.getPriority() == null
                || ticket.getPriority().isBlank()) {

            return "redirect:/create-ticket?error=true";
        }

        if (screenshot != null && !screenshot.isEmpty()) {

            if (!"image/png".equalsIgnoreCase(
                    screenshot.getContentType())) {

                return "redirect:/create-ticket?error=invalidFile";
            }

            try {

                ticket.setAttachment(screenshot.getBytes());
                ticket.setAttachmentFileName(
                        screenshot.getOriginalFilename());

            } catch (IOException e) {

                return "redirect:/create-ticket?error=fileUpload";
            }
        }

        // Get the actual logged-in employee
        User loggedInUser = getLoggedInUser(authentication);

        // Link this ticket to that exact employee
        ticket.setUser(loggedInUser);

        ticket.setStatus("Open");

        ServiceRequest savedTicket =
                ticketService.createTicket(ticket);

        // Go directly to the newly created ticket
        return "redirect:/ticket-details/" + savedTicket.getTicketId();
    }

    // ---------------------------------------------------------
    // MY TICKETS
    // ---------------------------------------------------------

    @GetMapping("/tickets")
    public String ticketsPage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Model model,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        // Only this employee's tickets
        List<ServiceRequest> tickets =
                ticketService.fetchTicketsByUser(loggedInUser);

        if (search != null && !search.isBlank()) {

            String value = search.toLowerCase();

            tickets = tickets.stream()
                    .filter(t ->
                            t.getIssue() != null
                                    && t.getIssue()
                                    .toLowerCase()
                                    .contains(value))
                    .toList();
        }

        if (status != null && !status.isBlank()) {

            tickets = tickets.stream()
                    .filter(t ->
                            status.equalsIgnoreCase(t.getStatus()))
                    .toList();
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("search", search);
        model.addAttribute("status", status);

        return "tickets";
    }

    // ---------------------------------------------------------
    // TICKET DETAILS
    // ---------------------------------------------------------

    @GetMapping("/ticket-details/{id}")
    public String ticketDetails(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        ServiceRequest ticket =
                ticketService.findTicketByIdAndUser(
                        id,
                        loggedInUser
                ).orElseThrow(
                        () -> new RuntimeException("Ticket not found")
                );

        model.addAttribute("ticket", ticket);

        return "ticket-details";
    }

    // ---------------------------------------------------------
    // UPDATE TICKET PAGE
    // ---------------------------------------------------------

    @GetMapping("/update-ticket/{id}")
    public String updateTicketPage(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        ServiceRequest ticket =
                ticketService.findTicketByIdAndUser(
                        id,
                        loggedInUser
                ).orElseThrow(
                        () -> new RuntimeException("Ticket not found")
                );

        model.addAttribute("ticket", ticket);

        return "update-ticket";
    }

    // ---------------------------------------------------------
    // UPDATE TICKET
    // ---------------------------------------------------------

    @PostMapping("/update-ticket/{id}")
    public String updateTicket(
            @PathVariable("id") Long id,
            @ModelAttribute("ticket") ServiceRequest ticket,
            @RequestParam(
                    value = "screenshot",
                    required = false)
            MultipartFile screenshot,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        // IMPORTANT:
        // Fetch only if this ticket belongs to this employee
        ServiceRequest existingTicket =
                ticketService.findTicketByIdAndUser(
                        id,
                        loggedInUser
                ).orElseThrow(
                        () -> new RuntimeException("Ticket not found")
                );

        if (ticket.getIssue() == null
                || ticket.getIssue().isBlank()
                || ticket.getIssueDescription() == null
                || ticket.getIssueDescription().isBlank()
                || ticket.getPriority() == null
                || ticket.getPriority().isBlank()) {

            return "redirect:/update-ticket/"
                    + id
                    + "?error=true";
        }

        // Handle new screenshot
        if (screenshot != null && !screenshot.isEmpty()) {

            if (!"image/png".equalsIgnoreCase(
                    screenshot.getContentType())) {

                return "redirect:/update-ticket/"
                        + id
                        + "?error=invalidFile";
            }

            try {

                existingTicket.setAttachment(
                        screenshot.getBytes());

                existingTicket.setAttachmentFileName(
                        screenshot.getOriginalFilename());

            } catch (IOException e) {

                return "redirect:/update-ticket/"
                        + id
                        + "?error=fileUpload";
            }
        }

        // Update only employee-editable fields
        existingTicket.setIssue(ticket.getIssue());
        existingTicket.setIssueDescription(
                ticket.getIssueDescription());
        existingTicket.setPriority(ticket.getPriority());

        // IMPORTANT:
        // Do NOT change the ticket owner.
        existingTicket.setUser(loggedInUser);

        ticketService.updateTicket(existingTicket);

        return "redirect:/ticket-details/" + id;
    }

    // ---------------------------------------------------------
    // DELETE TICKET
    // ---------------------------------------------------------

    @PostMapping("/delete-ticket/{id}")
    public String deleteTicket(
            @PathVariable Long id,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        // First verify ownership
        ServiceRequest ticket =
                ticketService.findTicketByIdAndUser(
                        id,
                        loggedInUser
                ).orElseThrow(
                        () -> new RuntimeException("Ticket not found")
                );

        // Delete only after ownership is confirmed
        ticketService.deleteTicket(ticket.getTicketId());

        return "redirect:/tickets";
    }

    // ---------------------------------------------------------
    // VIEW TICKET ATTACHMENT
    // ---------------------------------------------------------

    @GetMapping("/ticket-attachment/{id}")
    public ResponseEntity<byte[]> viewAttachment(
            @PathVariable Long id,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        // Only the owner can view the attachment
        ServiceRequest ticket =
                ticketService.findTicketByIdAndUser(
                        id,
                        loggedInUser
                ).orElseThrow(
                        () -> new RuntimeException("Ticket not found")
                );

        if (ticket.getAttachment() == null
                || ticket.getAttachment().length == 0) {

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(ticket.getAttachment());
    }

    // ---------------------------------------------------------
    // GET LOGGED-IN USER
    // ---------------------------------------------------------

    private User getLoggedInUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userService.getUsers()
                .stream()
                .filter(user ->
                        email.equalsIgnoreCase(user.getEmail()))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException(
                                "Logged-in user not found")
                );
    }
}