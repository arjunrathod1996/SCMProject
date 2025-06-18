package com.SCM.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.entities.Business;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.relation.CustomerRelation;
import com.SCM.relation.CustomerRelationRepository;
import com.SCM.repository.CustomerRepository;
import com.SCM.service.CommonService;
import com.SCM.service.CustomerService;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    CustomerRelationRepository customerRelationRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);


//    @PostMapping("/customer/update")
//    public ResponseEntity<String> updateCustomer(@RequestBody Customer updatedCustomer) {
//        try {
//            // Retrieve the existing customer from the database
//            Customer existingCustomer = customerService.getCustomerById(updatedCustomer.getId());
//            
//    
//            // Check if the customer exists
//            if (existingCustomer == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
//            }
//            
//            // Update the existing customer with the new data
//            existingCustomer.setFirstName(updatedCustomer.getFirstName());
//            existingCustomer.setLastName(updatedCustomer.getLastName());
//            existingCustomer.setMobileNumber(updatedCustomer.getMobileNumber());
//           // existingCustomer.setRegion(updatedCustomer.getRegion());
//            
////            if (updatedCustomer.getRegion() != null && updatedCustomer.getRegion().equals("")) {
////                updatedCustomer.setRegion(null);
////            }
//            
//            // Save the updated customer back to the database
//            customerService.updateCustomer(existingCustomer);
//            
//            return ResponseEntity.ok("Customer updated successfully");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating customer");
//        }
//    }
    
    
//    @PostMapping("/customer/update")
//    public ResponseEntity<String> updateOrCreateCustomer(@RequestBody Customer updatedCustomer) {
//        try {
//            // Retrieve the existing customer from the database
//            Customer existingCustomer = customerService.getCustomerById(updatedCustomer.getId());
//            
//            if (existingCustomer == null) {
//                // Customer does not exist, create a new one
//                //customerService.createCustomer(updatedCustomer);
//            	customerRepository.save(updatedCustomer);
//                return ResponseEntity.status(HttpStatus.CREATED).body("Customer created successfully");
//            } else {
//                // Customer exists, update the existing customer with the new data
//                existingCustomer.setFirstName(updatedCustomer.getFirstName());
//                existingCustomer.setLastName(updatedCustomer.getLastName());
//                existingCustomer.setMobileNumber(updatedCustomer.getMobileNumber());
//                existingCustomer.setGender(updatedCustomer.getGender());
//                // existingCustomer.setRegion(updatedCustomer.getRegion());
//
//                // Save the updated customer back to the database
//                customerService.updateCustomer(existingCustomer);
//                return ResponseEntity.ok("Customer updated successfully");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating or creating customer");
//        }
//    }
    
    
    @PostMapping("/customer/update")
    public ResponseEntity<String> updateOrCreateCustomer(@RequestBody Customer updatedCustomer) {
        try {
            // Retrieve the existing customer from the database
            Customer existingCustomer = customerService.getCustomerById(updatedCustomer.getId());
            
            if (existingCustomer == null) {
                // Customer does not exist, create a new one
                customerRepository.save(updatedCustomer);
                return ResponseEntity.status(HttpStatus.CREATED).body("Customer created successfully");
            } else {
                // Customer exists, update the existing customer with the new data
                existingCustomer.setFirstName(updatedCustomer.getFirstName());
                existingCustomer.setLastName(updatedCustomer.getLastName());
                existingCustomer.setMobileNumber(updatedCustomer.getMobileNumber());
                existingCustomer.setGender(updatedCustomer.getGender());
                // existingCustomer.setRegion(updatedCustomer.getRegion());

                // Save the updated customer back to the database
                customerService.updateCustomer(existingCustomer);
                return ResponseEntity.ok("Customer updated successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating or creating customer");
        }
    }
    
    
    
    
//    @GetMapping("/search")
//    public ResponseEntity<?> search(@RequestParam String query) {
//        // Logging the start of the search method
//        logger.info("Starting search for query: {}", query);
//
//        // Get the logged-in user
//        User loggedInUser = commonService.getLoggedInUser();
//
//        List<Customer> results = new ArrayList<>();
//        String redirectUrl = null;
//
//        if (loggedInUser != null) {
//            String role = loggedInUser.getRole().getName().name();
//
//            switch (role) {
//                case "ROLE_ADMIN":
//                    results = customerRepository.findByEmailContainingOrPhoneNumberContaining(query, query);
//                    break;
//                case "ROLE_MERCHANT_ADMIN":
//                    results = customerRepository.findByBusinessAndEmailContainingOrPhoneNumberContaining(loggedInUser.getBusiness().getId(), query, query);
//                    break;
//                case "ROLE_MERCHANT_STAFF":
//                    Business business = loggedInUser.getMerchant().getBusiness();
//                    results = customerRepository.findByBusinessAndEmailContainingOrPhoneNumberContaining(business.getId(), query, query);
//                    break;
//                default:
//                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
//            }
//        
//
//            if (!results.isEmpty()) {
//                // Assuming redirectUrl is the profile page of the first matched customer
//                Customer customer = results.get(0);
//                redirectUrl = "/customer-profile/" + customer.getId();
//                // Logging the redirection URL
//                logger.info("Redirecting to: {}", redirectUrl);
//            }
//        } else {
//            // Log unauthorized access
//            logger.warn("Unauthorized access: User not logged in");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
//        }
//
//        // Prepare response
//        Map<String, Object> response = new HashMap<>();
//        response.put("results", results);
//        response.put("redirectUrl", redirectUrl);
//
//        // Log the search results
//        logger.info("Search results: {}", results);
//
//        return ResponseEntity.ok(response);
//    }
    
    
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String query) {
        // Get the logged-in user
        User loggedInUser = commonService.getLoggedInUser();

        List<CustomerRelation> results = null;
        String redirectUrl = null;

        if (loggedInUser != null) {
            String role = loggedInUser.getRole().getName().name();
            Merchant merchant = null;
            Business business = null;
            
            switch (role) {
            case "ROLE_ADMIN":
                // Implement search logic for ROLE_ADMIN
                results = customerRelationRepository.findByEmailContainingOrPhoneNumberContaining(query, query);
                break;
            case "ROLE_MERCHANT_ADMIN":
                // Merchant admin can search customers related to their business
               // business = loggedInUser.getMerchant().getBusiness();
                results = customerRelationRepository.findByMerchantBusinessIdAndMerchantEmailAndCustomerEmailOrMobileNumber(loggedInUser.getBusiness().getId(), query, query);
                break;
            case "ROLE_MERCHANT_STAFF":
                // Merchant staff can search customers related to their merchant
                merchant = loggedInUser.getMerchant();
                business = loggedInUser.getMerchant().getBusiness();
                results = customerRelationRepository.findByMerchantBusinessIdAndMerchantEmailAndCustomerEmailOrMobileNumber(business.getId(), query,query);
                break;
            default:
                // Log unauthorized access
                logger.warn("Unauthorized access for user: {}", loggedInUser.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
        }


            if (results != null && !results.isEmpty()) {
                // Assuming redirectUrl is the profile page of the first matched customer
                CustomerRelation customerRelation = results.get(0);
                redirectUrl = "/customer-profile/" + customerRelation.getCustomer().getId();
                // Logging the redirection URL
                logger.info("Redirecting to: {}", redirectUrl);
            }
        } else {
            // Log unauthorized access
            logger.warn("Unauthorized access: User not logged in");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("redirectUrl", redirectUrl);

        return ResponseEntity.ok(response);
    }


}

