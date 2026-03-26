package com.example.E_COMMERCE.Backend.Repository;

import com.example.E_COMMERCE.Backend.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStockLessThan(int stock);

    List<Product> findBySellerTypeAndSellerId(String sellerType, Long sellerId);

    List<Product> findByNameContainingIgnoreCase(String name);
}
