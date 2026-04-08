package com.anne.portfolio_backend.controller;

import com.anne.portfolio_backend.model.ContactMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/send")
    public String sendMessage(@RequestBody ContactMessage contact) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo("atuyishime28@gmail.com");
            mail.setSubject("New Message from Portfolio - " + contact.getName());
            mail.setText(
                "You have a new message from your portfolio!\n\n" +
                "Name: " + contact.getName() + "\n" +
                "Email: " + contact.getEmail() + "\n\n" +
                "Message:\n" + contact.getMessage()
            );
            mail.setFrom("atuyishime28@gmail.com");
            mailSender.send(mail);
            return "Message sent successfully!";
        } catch (Exception e) {
            return "Error sending message: " + e.getMessage();
        }
    }
}