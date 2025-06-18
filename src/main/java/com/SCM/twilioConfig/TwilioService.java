package com.SCM.twilioConfig;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import lombok.Data;


@Service
@Configuration
@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioService {
    
    @Value("${twilio.account_sid}")
    private String accountSid;
    
    @Value("${twilio.auth_token}")
    private String authToken;
    
    @Value("${twilio.phone_number}")
    private String fromPhoneNumber;
    

    @jakarta.annotation.PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendOtp(String toPhoneNumber, String otp) {
        Message.creator(
                new com.twilio.type.PhoneNumber(toPhoneNumber),
                new com.twilio.type.PhoneNumber(fromPhoneNumber),
                "Your OTP is " + otp
        ).create();
    }

  
    
    
}
