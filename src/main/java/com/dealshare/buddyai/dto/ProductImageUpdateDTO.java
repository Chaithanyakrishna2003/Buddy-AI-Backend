package com.dealshare.buddyai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageUpdateDTO {
    // Map of product_id -> image_url
    private Map<Integer, String> imageMappings;
}





