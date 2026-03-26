package com.example.E_COMMERCE.Backend.ServiceImplementation;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.Payment;
import com.example.E_COMMERCE.Backend.Entity.Product;
import com.example.E_COMMERCE.Backend.Entity.User;
import com.example.E_COMMERCE.Backend.Entity.Vendor;
import com.example.E_COMMERCE.Backend.Repository.OrderRepository;
import com.example.E_COMMERCE.Backend.Repository.PaymentRepository;
import com.example.E_COMMERCE.Backend.Repository.ProductImageRepository;
import com.example.E_COMMERCE.Backend.Repository.ProductRepository;
import com.example.E_COMMERCE.Backend.Repository.UserRepository;
import com.example.E_COMMERCE.Backend.Repository.VendorRepository;
import com.example.E_COMMERCE.Backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    @Override
    public Product addProduct(Product product, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) throw new RuntimeException("User not found");
        product.setSellerType("USER");
        product.setSellerId(user.getUserId());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product, String userEmail) {
        Product existing = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findByEmail(userEmail);
        if (user == null || !existing.getSellerType().equals("USER") || !existing.getSellerId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setDiscount(product.getDiscount());
        existing.setStock(product.getStock());
        existing.setAvailable(product.isAvailable());
        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long productId, String userEmail) {
        Product existing = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findByEmail(userEmail);
        if (user == null || !existing.getSellerType().equals("USER") || !existing.getSellerId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        productRepository.delete(existing);
    }

    @Override
    public List<Product> getMyProducts(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) throw new RuntimeException("User not found");
        return productRepository.findBySellerTypeAndSellerId("USER", user.getUserId());
    }

    @Override
    public Order addToCart(Long productId, int quantity, String email) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        Order order = new Order();
        
        // Handle Buyer (could be USER or VENDOR)
        User userBuyer = userRepository.findByEmail(email);
        if (userBuyer != null) {
            order.setBuyerType("USER");
            order.setBuyerId(userBuyer.getUserId());
            order.setBuyerName(userBuyer.getName());
            order.setBuyerPhone(userBuyer.getPhoneNumber());
            order.setDeliveryAddress(userBuyer.getAddress());
        } else {
            Vendor vendorBuyer = vendorRepository.findByEmail(email);
            if (vendorBuyer != null) {
                order.setBuyerType("VENDOR");
                order.setBuyerId(vendorBuyer.getVendorId());
                order.setBuyerName(vendorBuyer.getName());
                order.setBuyerPhone(vendorBuyer.getPhoneNumber());
                order.setDeliveryAddress(vendorBuyer.getAddress());
            } else {
                throw new RuntimeException("Buyer not found");
            }
        }

        // Handle Seller
        order.setSellerType(product.getSellerType());
        order.setSellerId(product.getSellerId());
        if ("USER".equals(product.getSellerType())) {
            User s = userRepository.findById(product.getSellerId()).orElse(null);
            if (s != null) {
                order.setSellerName(s.getName());
                order.setSellerPhone(s.getPhoneNumber());
            }
        } else {
            Vendor v = vendorRepository.findById(product.getSellerId()).orElse(null);
            if (v != null) {
                order.setSellerName(v.getName());
                order.setSellerPhone(v.getPhoneNumber());
            }
        }

        order.setProduct(product);
        order.setProductName(product.getName());
        
        // Get the first image URL if available
        List<com.example.E_COMMERCE.Backend.Entity.ProductImage> images = productImageRepository.findByProduct(product);
        if (images != null && !images.isEmpty()) {
            order.setProductImage(images.get(0).getUrl());
        }
        
        order.setQuantity(quantity);
        double finalPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);
        order.setTotalAmount(finalPrice * quantity);
        order.setStatus("CART");

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getMyOrders(String email) {
        User u = userRepository.findByEmail(email);
        if (u != null) return orderRepository.findByBuyerTypeAndBuyerId("USER", u.getUserId());
        Vendor v = vendorRepository.findByEmail(email);
        if (v != null) return orderRepository.findByBuyerTypeAndBuyerId("VENDOR", v.getVendorId());
        throw new RuntimeException("Not found");
    }

    @Override
    public Order placeOrder(Long orderId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Auth check (buyer only)
        if (("USER".equals(order.getBuyerType()) && !userRepository.findById(order.getBuyerId()).get().getEmail().equals(email)) &&
            ("VENDOR".equals(order.getBuyerType()) && !vendorRepository.findById(order.getBuyerId()).get().getEmail().equals(email))) {
            throw new RuntimeException("Unauthorized");
        }

        if (!"CART".equals(order.getStatus())) {
            throw new RuntimeException("Order is not in CART status");
        }

        Product product = order.getProduct();
        if (product != null) {
            if (product.getStock() < order.getQuantity()) throw new RuntimeException("Insufficient stock");
            product.setStock(product.getStock() - order.getQuantity());
            productRepository.save(product);
        }

        order.setStatus("PLACED");
        order.setOrderTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public Payment makePayment(Long orderId, double amount, String paymentMode, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"PLACED".equals(order.getStatus())) {
             throw new RuntimeException("Order must be PLACED");
        }
        
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMode(paymentMode);
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(new Date());

        order.setStatus("PAID");
        order.setDeliveryTime(LocalDateTime.now().plusDays(3)); // Fake delivery date
        orderRepository.save(order);

        return paymentRepository.save(payment);
    }
}
