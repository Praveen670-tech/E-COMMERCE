package com.example.E_COMMERCE.Backend.Controller;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.Payment;
import com.example.E_COMMERCE.Backend.Entity.Product;
import com.example.E_COMMERCE.Backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    private com.example.E_COMMERCE.Backend.Repository.ProductImageRepository productImageRepository;

    @GetMapping("/products")
    public List<Map<String, Object>> getAllProducts() {
        List<Product> products = userService.getAllProducts();
        return products.stream().map(p -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("productId", p.getProductId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            map.put("price", p.getPrice());
            map.put("discount", p.getDiscount());
            map.put("stock", p.getStock());
            map.put("sellerType", p.getSellerType());
            map.put("available", p.isAvailable());
            
            // Add images
            List<com.example.E_COMMERCE.Backend.Entity.ProductImage> images = productImageRepository.findByProduct(p);
            map.put("images", images);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/products/search")
    public List<Product> searchProducts(@RequestParam String q) {
        return userService.searchProducts(q);
    }

    @PostMapping("/product")
    public Product addProduct(@RequestBody Product product, Principal principal) {
        return userService.addProduct(product, principal.getName());
    }

    @PutMapping("/product/{productId}")
    public Product updateProduct(@PathVariable Long productId, @RequestBody Product product, Principal principal) {
        return userService.updateProduct(productId, product, principal.getName());
    }

    @DeleteMapping("/product/{productId}")
    public void deleteProduct(@PathVariable Long productId, Principal principal) {
        userService.deleteProduct(productId, principal.getName());
    }

    @GetMapping("/my-products")
    public List<Product> getMyProducts(Principal principal) {
        return userService.getMyProducts(principal.getName()); 
    }

    @PostMapping("/cart")
    public Order addToCart(@RequestParam Long productId, @RequestParam int quantity, Principal principal) {
        return userService.addToCart(productId, quantity, principal.getName());
    }

    @GetMapping("/orders")
    public List<Order> getMyOrders(Principal principal) {
        return userService.getMyOrders(principal.getName());
    }

    @PostMapping("/order/{orderId}")
    public Order placeOrder(@PathVariable Long orderId, Principal principal) {
        return userService.placeOrder(orderId, principal.getName());
    }

    @PostMapping("/payment/{orderId}")
    public Payment makePayment(@PathVariable Long orderId, @RequestBody Map<String, Object> paymentData, Principal principal) {
        double amount = Double.parseDouble(paymentData.get("amount").toString());
        String paymentMode = paymentData.get("paymentMode").toString();
        return userService.makePayment(orderId, amount, paymentMode, principal.getName());
    }
}
