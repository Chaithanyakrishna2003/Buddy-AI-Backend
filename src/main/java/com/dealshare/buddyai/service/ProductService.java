package com.dealshare.buddyai.service;

import com.dealshare.buddyai.dto.ProductDetailsDTO;
import com.dealshare.buddyai.dto.ResponseDTO;
import com.dealshare.buddyai.model.Product;
import com.dealshare.buddyai.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Search products with filters
     */
    public ResponseDTO<List<ProductDetailsDTO>> searchProducts(
            String keyword, 
            String category,
            Double minPrice,
            Double maxPrice
    ) {
        try {
            Query query = new Query();
            
            // Keyword search (product_name, brand, description)
            if (keyword != null && !keyword.trim().isEmpty()) {
                Criteria keywordCriteria = new Criteria().orOperator(
                    Criteria.where("product_name").regex(keyword, "i"),
                    Criteria.where("brand").regex(keyword, "i"),
                    Criteria.where("description").regex(keyword, "i")
                );
                query.addCriteria(keywordCriteria);
            }
            
            // Category filter
            if (category != null && !category.trim().isEmpty() && !"All".equalsIgnoreCase(category)) {
                query.addCriteria(Criteria.where("category").is(category));
            }
            
            // Price range filter
            if (minPrice != null) {
                query.addCriteria(Criteria.where("price").gte(minPrice));
            }
            if (maxPrice != null) {
                query.addCriteria(Criteria.where("price").lte(maxPrice));
            }
            
            List<Product> products = mongoTemplate.find(query, Product.class);
            List<ProductDetailsDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            log.info("Found {} products matching search criteria", productDTOs.size());
            
            return ResponseDTO.<List<ProductDetailsDTO>>builder()
                    .success(true)
                    .message("Found " + productDTOs.size() + " product(s)")
                    .data(productDTOs)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error searching products", e);
            return ResponseDTO.<List<ProductDetailsDTO>>builder()
                    .success(false)
                    .message("Error searching products: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * Get product by ID
     */
    public ResponseDTO<ProductDetailsDTO> getProductById(Integer productId) {
        try {
            Product product = productRepository.findByProductId(productId);
            
            if (product == null) {
                return ResponseDTO.<ProductDetailsDTO>builder()
                        .success(false)
                        .message("Product not found with ID: " + productId)
                        .data(null)
                        .build();
            }
            
            return ResponseDTO.<ProductDetailsDTO>builder()
                    .success(true)
                    .message("Product found")
                    .data(convertToDTO(product))
                    .build();
                    
        } catch (Exception e) {
            log.error("Error fetching product", e);
            return ResponseDTO.<ProductDetailsDTO>builder()
                    .success(false)
                    .message("Error fetching product: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * Get all products
     */
    public ResponseDTO<List<ProductDetailsDTO>> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            List<ProductDetailsDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} total products", productDTOs.size());
            
            return ResponseDTO.<List<ProductDetailsDTO>>builder()
                    .success(true)
                    .message("Retrieved " + productDTOs.size() + " product(s)")
                    .data(productDTOs)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error fetching all products", e);
            return ResponseDTO.<List<ProductDetailsDTO>>builder()
                    .success(false)
                    .message("Error fetching products: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * Get product recommendations
     */
    public ResponseDTO<List<ProductDetailsDTO>> getRecommendations(Integer limit) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("is_popular").is(true));
            query.limit(limit != null ? limit : 10);
            
            List<Product> products = mongoTemplate.find(query, Product.class);
            List<ProductDetailsDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseDTO.<List<ProductDetailsDTO>>builder()
                    .success(true)
                    .message("Retrieved " + productDTOs.size() + " recommendation(s)")
                    .data(productDTOs)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error fetching recommendations", e);
            return ResponseDTO.<List<ProductDetailsDTO>>builder()
                    .success(false)
                    .message("Error fetching recommendations: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * Update product images in bulk
     */
    public ResponseDTO<String> updateProductImages(Map<Integer, String> imageMappings) {
        try {
            int updatedCount = 0;
            for (Map.Entry<Integer, String> entry : imageMappings.entrySet()) {
                Product product = productRepository.findByProductId(entry.getKey());
                if (product != null) {
                    product.setImageUrl(entry.getValue());
                    product.setUpdatedAt(LocalDateTime.now());
                    productRepository.save(product);
                    updatedCount++;
                }
            }
            return ResponseDTO.<String>builder()
                    .success(true)
                    .message("Updated images for " + updatedCount + " products")
                    .data("Success")
                    .build();
        } catch (Exception e) {
            log.error("Error updating product images", e);
            return ResponseDTO.<String>builder()
                    .success(false)
                    .message("Error updating product images: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * Convert Product entity to DTO
     */
    private ProductDetailsDTO convertToDTO(Product product) {
        return ProductDetailsDTO.builder()
                .product_id(product.getProductId())
                .product_name(product.getProductName())
                .brand(product.getBrand())
                .category(product.getCategory() != null ? product.getCategory() : "General")
                .price(product.getPrice())
                .discounted_price(product.getDiscountedPrice())
                .available_stock(product.getAvailableStock() != null ? product.getAvailableStock() : 0)
                .sku_code(product.getSkuCode())
                .image_url(product.getImageUrl() != null ? product.getImageUrl() : 
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop")
                .description(product.getDescription())
                .rating(product.getRating() != null ? product.getRating() : 4.5)
                .is_popular(product.getIsPopular() != null ? product.getIsPopular() : false)
                .build();
    }
}



