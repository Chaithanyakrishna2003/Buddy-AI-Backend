package com.dealshare.buddyai.repository;

import com.dealshare.buddyai.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderId(int orderId);
    List<Order> findByUserId(int userId);
}







