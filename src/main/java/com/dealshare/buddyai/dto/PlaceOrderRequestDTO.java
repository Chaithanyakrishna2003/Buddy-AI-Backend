package com.dealshare.buddyai.dto;

import com.dealshare.buddyai.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderRequestDTO {
    private int userId;
    private List<OrderItem> orderItems;
    private String paymentMethod;
    private String deliveryAddress;
}



