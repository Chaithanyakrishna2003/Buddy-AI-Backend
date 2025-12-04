package com.dealshare.buddyai.repository;

import com.dealshare.buddyai.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    /**
     * Find product by product_id
     */
    Product findByProductId(Integer productId);
    
    /**
     * Find products by category
     */
    List<Product> findByCategory(String category);
    
    /**
     * Find popular products
     */
    List<Product> findByIsPopularTrue();
    
    /**
     * Search products by name or brand
     */
    @Query("{ $or: [ { 'product_name': { $regex: ?0, $options: 'i' } }, { 'brand': { $regex: ?0, $options: 'i' } } ] }")
    List<Product> searchByNameOrBrand(String keyword);
    
    /**
     * Find products by price range
     */
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
}

