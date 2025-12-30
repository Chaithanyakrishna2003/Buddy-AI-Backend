package com.dealshare.buddyai.controller;

import com.dealshare.buddyai.dto.SupportTicketRequestDTO;
import com.dealshare.buddyai.dto.SupportTicketResponseDTO;
import com.dealshare.buddyai.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support-tickets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "https://*.vercel.app"})
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    public ResponseEntity<SupportTicketResponseDTO> createTicket(@RequestBody SupportTicketRequestDTO request) {
        try {
            log.info("Received create ticket request for order: {}", request.getOrderId());
            SupportTicketResponseDTO response = supportTicketService.createTicket(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating support ticket", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<SupportTicketResponseDTO>> getTicketsByOrderId(@PathVariable String orderId) {
        try {
            log.info("Fetching tickets for order: {}", orderId);
            List<SupportTicketResponseDTO> tickets = supportTicketService.getTicketsByOrderId(orderId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Error fetching tickets for order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SupportTicketResponseDTO>> getTicketsByUserId(@PathVariable int userId) {
        try {
            log.info("Fetching tickets for user: {}", userId);
            List<SupportTicketResponseDTO> tickets = supportTicketService.getTicketsByUserId(userId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Error fetching tickets for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}/count")
    public ResponseEntity<Long> getTicketCountByOrderId(@PathVariable String orderId) {
        try {
            long count = supportTicketService.getTicketCountByOrderId(orderId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error counting tickets for order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

