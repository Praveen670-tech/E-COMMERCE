package com.example.E_COMMERCE.Backend.ServiceImplementation;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Repository.OrderRepository;
import com.example.E_COMMERCE.Backend.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImplementation implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
}
