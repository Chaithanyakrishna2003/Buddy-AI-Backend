package com.dealshare.buddyai.service;

import com.dealshare.buddyai.dto.SupportTicketRequestDTO;
import com.dealshare.buddyai.dto.SupportTicketResponseDTO;
import com.dealshare.buddyai.model.SupportTicket;
import com.dealshare.buddyai.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketResponseDTO createTicket(SupportTicketRequestDTO request) {
        log.info("Creating support ticket for order: {}", request.getOrderId());

        // Generate meaningful ticket ID based on order ID and ticket count
        long ticketCount = supportTicketRepository.countByOrderId(request.getOrderId());
        String ticketId = String.format("TKT-%s-%d", request.getOrderId(), ticketCount + 1);

        List<SupportTicket.PhotoMetadata> photoMetadata = request.getPhotos() != null ?
            request.getPhotos().stream()
                .map(dto -> SupportTicket.PhotoMetadata.builder()
                    .name(dto.getName())
                    .size(dto.getSize())
                    .type(dto.getType())
                    .lastModified(dto.getLastModified())
                    .url(dto.getUrl()) // Store base64 data URL if provided
                    .build())
                .collect(Collectors.toList()) : List.of();

        SupportTicket ticket = SupportTicket.builder()
            .ticketId(ticketId)
            .orderId(request.getOrderId())
            .userId(request.getUserId())
            .issueType(request.getIssueType())
            .selectedItemNames(request.getSelectedItemNames())
            .itemsCount(request.getItemsCount())
            .comment(request.getComment())
            .photos(photoMetadata)
            .photoCount(photoMetadata.size())
            .status("resolved")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .resolvedAt(LocalDateTime.now())
            .resolution("Thank you for reporting. Our team will review and get back to you within 24 hours.")
            .build();

        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        log.info("Created ticket: {}", savedTicket.getTicketId());

        return convertToDTO(savedTicket);
    }

    public List<SupportTicketResponseDTO> getTicketsByOrderId(String orderId) {
        log.info("Fetching tickets for order: {}", orderId);
        List<SupportTicket> tickets = supportTicketRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
        return tickets.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<SupportTicketResponseDTO> getTicketsByUserId(int userId) {
        log.info("Fetching tickets for user: {}", userId);
        List<SupportTicket> tickets = supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return tickets.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public long getTicketCountByOrderId(String orderId) {
        return supportTicketRepository.countByOrderId(orderId);
    }

    private SupportTicketResponseDTO convertToDTO(SupportTicket ticket) {
        List<SupportTicketResponseDTO.PhotoMetadataDTO> photoDTOs = ticket.getPhotos() != null ?
            ticket.getPhotos().stream()
                .map(photo -> SupportTicketResponseDTO.PhotoMetadataDTO.builder()
                    .name(photo.getName())
                    .size(photo.getSize())
                    .type(photo.getType())
                    .lastModified(photo.getLastModified())
                    .url(photo.getUrl())
                    .build())
                .collect(Collectors.toList()) : List.of();

        return SupportTicketResponseDTO.builder()
            .id(ticket.getId())
            .ticketId(ticket.getTicketId())
            .orderId(ticket.getOrderId())
            .userId(ticket.getUserId())
            .issueType(ticket.getIssueType())
            .selectedItemNames(ticket.getSelectedItemNames())
            .itemsCount(ticket.getItemsCount())
            .comment(ticket.getComment())
            .photos(photoDTOs)
            .photoCount(ticket.getPhotoCount())
            .status(ticket.getStatus())
            .resolution(ticket.getResolution())
            .createdAt(ticket.getCreatedAt())
            .updatedAt(ticket.getUpdatedAt())
            .resolvedAt(ticket.getResolvedAt())
            .build();
    }
}
