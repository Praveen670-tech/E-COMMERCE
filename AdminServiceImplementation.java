package com.example.E_COMMERCE.Backend.ServiceImplementation;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.Payment;
import com.example.E_COMMERCE.Backend.Entity.User;
import com.example.E_COMMERCE.Backend.Entity.Vendor;
import com.example.E_COMMERCE.Backend.Repository.OrderRepository;
import com.example.E_COMMERCE.Backend.Repository.PaymentRepository;
import com.example.E_COMMERCE.Backend.Repository.UserRepository;
import com.example.E_COMMERCE.Backend.Repository.VendorRepository;
import com.example.E_COMMERCE.Backend.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImplementation implements AdminService {

    @Autowired private UserRepository userRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }
    
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
