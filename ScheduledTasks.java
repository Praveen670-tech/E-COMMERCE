package com.example.E_COMMERCE.Backend.Scheduler;

import com.example.E_COMMERCE.Backend.Entity.Product;
import com.example.E_COMMERCE.Backend.Entity.User;
import com.example.E_COMMERCE.Backend.Entity.Vendor;
import com.example.E_COMMERCE.Backend.Repository.ProductRepository;
import com.example.E_COMMERCE.Backend.Repository.UserRepository;
import com.example.E_COMMERCE.Backend.Repository.VendorRepository;
import com.example.E_COMMERCE.Backend.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    // Run every 5 minutes
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkProductAvailability() {
        try {
            List<Product> lowStockProducts = productRepository.findByStockLessThan(10);
            for (Product product : lowStockProducts) {
                String sellerEmail = null;
                if ("VENDOR".equals(product.getSellerType())) {
                    Vendor vendor = vendorRepository.findById(product.getSellerId()).orElse(null);
                    if (vendor != null) sellerEmail = vendor.getEmail();
                } else if ("USER".equals(product.getSellerType())) {
                    User user = userRepository.findById(product.getSellerId()).orElse(null);
                    if (user != null) sellerEmail = user.getEmail();
                }

                if (sellerEmail == null) continue;

                String subject = "Low Stock Alert: " + product.getName();
                String body = "Dear Seller,\n\nYour product '" + product.getName() + "' is running low on stock. Current stock: " + product.getStock() + ".\n\nPlease restock soon.\n\nBest Regards,\nE-Commerce Team";
                
                // Send email to vendor
                try {
                    emailService.sendEmail(sellerEmail, subject, body);
                    System.out.println("Low stock alert sent to " + sellerEmail);
                } catch (Exception e) {
                    System.err.println("Failed to send email to " + sellerEmail + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error in checkProductAvailability: " + e.getMessage());
        }
    }

    // Run every 10 minutes to simulate payment confirmations or other checks
    @Scheduled(cron = "0 */10 * * * ?")
    public void sendPaymentConfirmation() {
        // In a real scenario, we would fetch unprocessed payments. 
        // For now, we just log that the service is active.
        System.out.println("Payment confirmation service is active.");
    }
}
