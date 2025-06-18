package com.SCM.kafka;

import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Service
@Conditional(KafkaEnabledCondition.class)
public class LeadKafkaConsumer {

    @KafkaListener(topics = "leads_topic", groupId = "group_id")
    public void consumeLeadMessage(String message) {
        // Process the lead data (e.g., update database, notify sales team)
        System.out.println("Consumed message: " + message);
    }
}

//@Service
//public class LeadKafkaConsumer {
//
//    private final JavaMailSender emailSender;
//
//    // Constructor to inject the JavaMailSender bean
//    public LeadKafkaConsumer(JavaMailSender emailSender) {
//        this.emailSender = emailSender;
//    }
//
//    @KafkaListener(topics = "leads_topic", groupId = "group_id")
//    public void consumeLeadMessage(String message) {
//        System.out.println("Consumed message: " + message);
//
//        // Send an alert email
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo("admin@example.com");  // Replace with recipient email
//        mailMessage.setSubject("New Kafka Message");
//        mailMessage.setText("New message from Kafka topic: " + message);
//        emailSender.send(mailMessage);
//    }

//}
