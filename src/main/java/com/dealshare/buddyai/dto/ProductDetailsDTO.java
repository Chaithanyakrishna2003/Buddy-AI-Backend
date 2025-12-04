package com.dealshare.buddyai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsDTO {
    private Integer product_id;
    private String product_name;
    private String brand;
    private String category;
    private Double price;
    private Double discounted_price;
    private Integer available_stock;
    private String sku_code;
    private String image_url;
    private String description;
    private Double rating;
    private Boolean is_popular;
}
