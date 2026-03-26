package com.example.E_COMMERCE.Backend.Repository;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerTypeAndBuyerId(String buyerType, Long buyerId);

    List<Order> findBySellerTypeAndSellerId(String sellerType, Long sellerId);

    List<Order> findByProduct(Product product);
}
