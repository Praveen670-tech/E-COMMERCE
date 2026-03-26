package com.example.E_COMMERCE.Backend.Controller;

import com.example.E_COMMERCE.Backend.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class MailTestController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/mail")
    public String testMail() {
        emailService.sendEmail(
            "tnraj82442@gmail.com",
            "Test Mail",
            "Mail working successfully 🚀"
        );
        return "Mail sent";
    }
}