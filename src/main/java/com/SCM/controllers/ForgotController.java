package com.SCM.controllers;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController {
	
	Logger logger = LoggerFactory.getLogger(ForgotController.class);
	private static final SecureRandom random = new SecureRandom();
	
	@GetMapping("/email/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		 	String otp = generateOTP();
	        logger.debug("Email... {} ", email);
	        logger.debug("Generated OTP... {}", otp);
	        System.out.println("Email... " + email);
	        System.out.println("Generated OTP... " + otp);
		return "verify_otp";
	}
	
	private String generateOTP() {
        int otp = random.nextInt(9000) + 1000; // Generates a random 4-digit number between 1000 and 9999
        return String.valueOf(otp);
    }

}
