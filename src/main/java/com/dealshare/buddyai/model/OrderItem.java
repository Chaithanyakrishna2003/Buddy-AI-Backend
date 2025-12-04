package com.dealshare.buddyai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Field("product_id")
    @JsonProperty("productId")
    private int productId;
    
    @Field("product_name")
    @JsonProperty("productName")
    private String productName;
    
    @Field("image_url")
    @JsonProperty("imageUrl")
    private String imageUrl;
    
    @JsonProperty("quantity")
    private int quantity;
    
    @Field("unit_price")
    @JsonProperty("unitPrice")
    private double unitPrice;
    
    @Field("total_price")
    @JsonProperty("totalPrice")
    private double totalPrice;
    
    @JsonProperty("status")
    private String status;
}

