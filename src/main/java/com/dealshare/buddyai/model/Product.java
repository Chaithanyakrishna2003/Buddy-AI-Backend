package com.dealshare.buddyai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    
    @Id
    private String id;
    
    @Field("product_id")
    private Integer productId;
    
    @Field("product_name")
    private String productName;
    
    @Field("brand")
    private String brand;
    
    @Field("category")
    private String category;
    
    @Field("price")
    private Double price;
    
    @Field("discounted_price")
    private Double discountedPrice;
    
    @Field("available_stock")
    private Integer availableStock;
    
    @Field("sku_code")
    private String skuCode;
    
    @Field("image_url")
    private String imageUrl;
    
    @Field("description")
    private String description;
    
    @Field("rating")
    private Double rating;
    
    @Field("is_popular")
    private Boolean isPopular;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
}


