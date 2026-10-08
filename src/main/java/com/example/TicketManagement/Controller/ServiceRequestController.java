package com.example.TicketManagement.Controller;



import com.example.TicketManagement.Entity.ServiceRequest;

import com.example.TicketManagement.Service.TicketService;

import org.springframework.web.bind.annotation.*;



import java.util.List;

import java.util.Optional;



@RestController

public class ServiceRequestController {



    private TicketService service;



    public ServiceRequestController(TicketService service) {

        this.service = service;

    }



    @PostMapping("/create")

    public ServiceRequest creation(@RequestBody ServiceRequest request){

        return service.createTicket(request);

    }



    @GetMapping("/read")

    public List<ServiceRequest> read(){

        return service.fetchTickets();

    }



    @GetMapping("/read/{id}")

    public Optional<ServiceRequest> readById(@PathVariable Long id){

        return service.findTicketById(id);

    }



    @PutMapping("/update")

    public ServiceRequest update(@RequestBody ServiceRequest request){

        return service.updateTicket(request);

    }



    @DeleteMapping("/delete/{id}")

    public void delete(@PathVariable Long id){

        service.deleteTicket(id);

    }









}

