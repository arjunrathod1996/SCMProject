package com.SCM.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Conditional(KafkaEnabledCondition.class)
public class LeadKafkaProducer {

    private static final String LEAD_TOPIC = "leads_topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public LeadKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLeadMessage(String message) {
        kafkaTemplate.send(LEAD_TOPIC, message);
    }
}