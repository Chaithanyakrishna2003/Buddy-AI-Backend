package com.dealshare.buddyai.controller;

import com.dealshare.buddyai.dto.PlaceOrderRequestDTO;
import com.dealshare.buddyai.dto.PlaceOrderResponseDTO;
import com.dealshare.buddyai.dto.ResponseDTO;
import com.dealshare.buddyai.model.Order;
import com.dealshare.buddyai.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<PlaceOrderResponseDTO>> placeOrder(@RequestBody PlaceOrderRequestDTO request) {
        try {
            System.out.println("Received order request: " + request);
            PlaceOrderResponseDTO response = orderService.placeOrder(request);
            return ResponseEntity.ok(new ResponseDTO<>(true, "Order placed successfully", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(false, "Error placing order: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<Object>> getAllOrders() {
        try {
            System.out.println("Fetching all orders");
            Object orders = orderService.getAllOrders();
            return ResponseEntity.ok(new ResponseDTO<>(true, "Orders fetched successfully", orders));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(false, "Error fetching orders: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDTO<Order>> getOrderById(@PathVariable int orderId) {
        try {
            System.out.println("Fetching order by ID: " + orderId);
            Optional<Order> order = orderService.getOrderById(orderId);
            if (order.isPresent()) {
                return ResponseEntity.ok(new ResponseDTO<>(true, "Order found", order.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO<>(false, "Order not found", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(false, "Error fetching order: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<ResponseDTO<Object>> getOrderItems(@PathVariable int orderId) {
        try {
            System.out.println("Fetching order items for order ID: " + orderId);
            Optional<Order> order = orderService.getOrderById(orderId);
            if (order.isPresent()) {
                return ResponseEntity.ok(new ResponseDTO<>(true, "Order items found", order.get().getOrderItems()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO<>(false, "Order not found", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(false, "Error fetching order items: " + e.getMessage(), null));
        }
    }
}
