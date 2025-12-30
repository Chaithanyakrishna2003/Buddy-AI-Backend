package com.dealshare.buddyai.repository;

import com.dealshare.buddyai.model.SupportTicket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends MongoRepository<SupportTicket, String> {
    List<SupportTicket> findByOrderIdOrderByCreatedAtDesc(String orderId);
    List<SupportTicket> findByOrderIdOrderByCreatedAtAsc(String orderId);
    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(int userId);
    List<SupportTicket> findByStatus(String status);
    long countByOrderId(String orderId);
    Optional<SupportTicket> findByTicketId(String ticketId);
}

