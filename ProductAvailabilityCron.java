package com.example.E_COMMERCE.Backend.Scheduler;

import com.example.E_COMMERCE.Backend.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProductAvailabilityCron {

    @Autowired
    private EmailService emailService;

    // Every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkProductAvailability() {

        emailService.sendEmail(
                "tnraj82442@gmail.com",
                "Product Stock Alert",
                "Some products are running low on stock. Please restock."
        );

        System.out.println("Product availability email sent");
    }
}
