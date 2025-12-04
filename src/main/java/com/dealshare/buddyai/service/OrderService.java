package com.dealshare.buddyai.service;

import com.dealshare.buddyai.dto.PlaceOrderRequestDTO;
import com.dealshare.buddyai.dto.PlaceOrderResponseDTO;
import com.dealshare.buddyai.model.Order;
import com.dealshare.buddyai.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public PlaceOrderResponseDTO placeOrder(PlaceOrderRequestDTO request) {
        // Generate sequential order ID - get max and increment by 1
        int orderId = orderRepository.findAll().stream()
                .mapToInt(Order::getOrderId)
                .max()
                .orElse(10000) + 1;

        // Calculate total amount
        double totalAmount = request.getOrderItems().stream()
                .mapToDouble(item -> item.getTotalPrice())
                .sum();

        // Create order
        Order order = Order.builder()
                .orderId(orderId)
                .userId(request.getUserId())
                .orderItems(request.getOrderItems())
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .deliveryAddress(request.getDeliveryAddress())
                .status("Ordered")
                .orderDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save order to database
        orderRepository.save(order);

        // Return response
        return PlaceOrderResponseDTO.builder()
                .orderId(orderId)
                .totalAmount(totalAmount)
                .status("Ordered")
                .message("Order placed successfully")
                .build();
    }

    public Object getAllOrders() {
        // Return all orders from database
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(int orderId) {
        return orderRepository.findByOrderId(orderId);
    }
}

