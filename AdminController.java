package com.example.E_COMMERCE.Backend.Controller;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.User;
import com.example.E_COMMERCE.Backend.Entity.Vendor;
import com.example.E_COMMERCE.Backend.Repository.OrderRepository;
import com.example.E_COMMERCE.Backend.Repository.UserRepository;
import com.example.E_COMMERCE.Backend.Repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/vendors")
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @GetMapping("/purchases")
    public List<Order> getAllPurchases() {
        return orderRepository.findAll();
    }

    @GetMapping("/orders/{buyerType}/{buyerId}")
    public List<Order> getOrdersByAccount(@PathVariable String buyerType, @PathVariable Long buyerId) {
        return orderRepository.findByBuyerTypeAndBuyerId(buyerType, buyerId);
    }
}
