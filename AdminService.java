package com.example.E_COMMERCE.Backend.Service;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.Payment;
import com.example.E_COMMERCE.Backend.Entity.User;
import com.example.E_COMMERCE.Backend.Entity.Vendor;

import java.util.List;

public interface AdminService {

    List<User> getAllUsers();

    List<Vendor> getAllVendors();
    
    List<Order> getAllOrders();
    
    List<Payment> getAllPayments();
}
