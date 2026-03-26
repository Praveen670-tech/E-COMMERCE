package com.example.E_COMMERCE.Backend.Service;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.Product;
import com.example.E_COMMERCE.Backend.Entity.Payment;
import java.util.List;

public interface UserService {

    List<Product> getAllProducts();
    List<Product> searchProducts(String query);

    Product addProduct(Product product, String userEmail);
    Product updateProduct(Long productId, Product product, String userEmail);
    void deleteProduct(Long productId, String userEmail);
    List<Product> getMyProducts(String userEmail);

    Order addToCart(Long productId, int quantity, String email);

    List<Order> getMyOrders(String email);

    Order placeOrder(Long orderId, String email);

    Payment makePayment(Long orderId, double amount, String paymentMode, String email);
}
