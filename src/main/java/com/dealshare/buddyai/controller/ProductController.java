package com.dealshare.buddyai.controller;

import com.dealshare.buddyai.dto.ProductDetailsDTO;
import com.dealshare.buddyai.dto.ProductImageUpdateDTO;
import com.dealshare.buddyai.dto.ResponseDTO;
import com.dealshare.buddyai.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://*.vercel.app"})
public class ProductController {

    private final ProductService productService;

    /**
     * Search products with optional filters
     * GET /api/products/search?keyword=sugar&category=Grocery
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<List<ProductDetailsDTO>>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        log.info("Searching products - keyword: {}, category: {}", keyword, category);
        
        ResponseDTO<List<ProductDetailsDTO>> response = productService.searchProducts(
            keyword, category, minPrice, maxPrice
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get single product by ID
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProductDetailsDTO>> getProduct(@PathVariable Integer id) {
        log.info("Fetching product with ID: {}", id);
        
        ResponseDTO<ProductDetailsDTO> response = productService.getProductById(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get product recommendations
     * GET /api/products/recommendations
     */
    @GetMapping("/recommendations")
    public ResponseEntity<ResponseDTO<List<ProductDetailsDTO>>> getRecommendations(
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        log.info("Fetching {} product recommendations", limit);
        
        ResponseDTO<List<ProductDetailsDTO>> response = productService.getRecommendations(limit);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all products (no filters)
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<ResponseDTO<List<ProductDetailsDTO>>> getAllProducts() {
        log.info("Fetching all products");
        
        ResponseDTO<List<ProductDetailsDTO>> response = productService.getAllProducts();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update product images in bulk
     * POST /api/products/update-images
     * Body: { "imageMappings": { "1": "https://...", "2": "https://...", ... } }
     */
    @PostMapping("/update-images")
    public ResponseEntity<ResponseDTO<String>> updateProductImages(@RequestBody ProductImageUpdateDTO request) {
        log.info("Updating product images for {} products", request.getImageMappings().size());
        
        ResponseDTO<String> response = productService.updateProductImages(request.getImageMappings());
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}



