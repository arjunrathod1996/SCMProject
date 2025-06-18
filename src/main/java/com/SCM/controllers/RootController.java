package com.SCM.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.helpers.Helper;
import com.SCM.repository.CustomerRepository;
import com.SCM.service.CommonService;
import com.SCM.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class RootController {

    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    @Autowired
    UserServiceImpl userService;
    
    @Autowired
    CommonService commonService;
    
    @Autowired
    CustomerRepository customerRepository;

    @ModelAttribute
    public void loggedInUserInformation(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("user", null);
            return;
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        if (username == null) {
            model.addAttribute("user", null);
            return;
        }

        Optional<User> userOptional = userService.findByEmail(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.debug("User found: {}", user);
            model.addAttribute("user", user);
        } else {
            logger.error("User not found with email: {}", username);
            model.addAttribute("user", null);
            // Handle user not found scenario
            // For example, you might redirect to an error page or show a message on the current page
        }
        
        User loggedInUser = commonService.getLoggedInUser();
        Customer customer = null;

        if (loggedInUser != null) {
            if (loggedInUser.getPhoneNumber() != null) {
                // If the user is logged in with a phone number
                List<Customer> customers = customerRepository.findByMobileNumber(loggedInUser.getPhoneNumber());
                if (!customers.isEmpty()) {
                    // Assuming there is only one customer per phone number
                    customer = customers.get(0);
                }
            } else if (loggedInUser.getEmail() != null) {
                // If the user is logged in with an email
//                List<Customer> customers = customerRepository.findByEmail(user.getEmail());
//                if (!customers.isEmpty()) {
//                    // Assuming there is only one customer per email
//                    customer = customers.get(0);
//                }
            	customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
            }
            
            model.addAttribute("customer", customer);
        }
    }
}
