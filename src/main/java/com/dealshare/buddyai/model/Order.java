package com.dealshare.buddyai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private String id;

    @Field("order_id")
    private int orderId;

    @Field("user_id")
    private int userId;

    @Field("order_items")
    private List<OrderItem> orderItems;

    @Field("total_amount")
    private double totalAmount;

    @Field("payment_method")
    private String paymentMethod;

    @Field("delivery_address")
    private String deliveryAddress;

    private String status;

    @Field("order_date")
    private LocalDateTime orderDate;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
