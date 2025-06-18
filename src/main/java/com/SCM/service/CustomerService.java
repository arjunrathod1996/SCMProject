package com.SCM.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SCM.controllers.Country;
import com.SCM.controllers.Merchant;
import com.SCM.controllers.Region;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.relation.CustomerRelationService;
import com.SCM.repository.CountryRepository;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.UserRepository;

@Service
public class CustomerService {
	
	@Autowired
    private CustomerRepository customerRepository;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	CustomerRelationService customerRelationService;
	
	@Autowired
	UserRepository userRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
	
	// Method to retrieve a customer by ID
    public Customer getCustomerById(Long customerId) {
        try {
            return customerRepository.findById(customerId).orElse(null);
        } catch (Exception e) {
            logger.error("Error retrieving customer with ID: {}", customerId, e);
            throw new RuntimeException("Error retrieving customer", e);
        }
    }

    // Method to update a customer
    public Customer updateCustomer(Customer updatedCustomer) {
        try {
            // Ensure that the customer exists
            Customer existingCustomer = getCustomerById(updatedCustomer.getId());
            if (existingCustomer == null) {
                throw new IllegalArgumentException("Customer not found with ID: " + updatedCustomer.getId());
            }

            // Update the existing customer with the new data
            existingCustomer.setFirstName(updatedCustomer.getFirstName());
            existingCustomer.setLastName(updatedCustomer.getLastName());
            existingCustomer.setMobileNumber(updatedCustomer.getMobileNumber());

            // Save the updated customer back to the database
            return customerRepository.save(existingCustomer);
        } catch (IllegalArgumentException e) {
            logger.warn("Customer not found with ID: {}", updatedCustomer.getId(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating customer: {}", updatedCustomer, e);
            throw new RuntimeException("Error updating customer", e);
        }
    }

    // Method to save a customer
    public Customer save(Customer customer, Merchant registrar) {
        return save(customer, registrar, false, true);
    }

    public Customer save(Customer customer, Merchant registrar, boolean isBulkImport, boolean sendNotification) {
        User createdBy = null;
        try {
            if (registrar != null) {
                List<User> users = userRepository.findByMerchant(registrar);
                if (users != null && !users.isEmpty()) {
                    createdBy = users.get(0);
                }
            }
            return save(customer, registrar, createdBy, isBulkImport, sendNotification);
        } catch (Exception e) {
            logger.error("Error saving customer with registrar: {}", registrar, e);
            throw new RuntimeException("Error saving customer", e);
        }
    }

    public Customer save(Customer customer, Merchant registrar, User createdBy) {
        return save(customer, registrar, createdBy, false, true);
    }

    public Customer save(Customer customer, Merchant registrar, User createdBy, boolean isBulkImport, boolean sendNotification) {
        try {
            if (customer.getId() != null) {
                final Long customerId = customer.getId(); // Temporary final variable
                Customer original = customerRepository.findById(customerId).orElseThrow(() ->
                    new IllegalArgumentException("Customer not found with ID: " + customerId));
                original.setFirstName(customer.getFirstName());
                original.setLastName(customer.getLastName());
                original.setEmail(customer.getEmail());
                if (customer.getRegion() != null) {
                    original.setRegion(customer.getRegion());
                }
                customer = customerRepository.save(original);

            } else {
                Country country = commonService.getDefaultCountry();
                if (customer.getCountryID() != null) {
                    country = countryRepository.findById(customer.getCountryID()).orElse(country);
                } else {
                    if (registrar != null)
                        country = Optional.ofNullable(registrar.getRegion()).map(Region::getCountry).orElse(null);
                }
                List<Customer> existing = customerRepository.findByMobileNumber(customer.getMobileNumber());
                if (existing != null && !existing.isEmpty()) {
                    return existing.get(0);
                }
                customer.setCountry(country);
                customer = customerRepository.save(customer);
                if (registrar != null) {
                    customerRelationService.add(registrar, customer);
                }
            }
            logger.info("Customer saved successfully: {}", customer);
            return customer;
        } catch (IllegalArgumentException e) {
            logger.warn("Error saving customer: {}", customer, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error saving customer: {}", customer, e);
            throw new RuntimeException("Error saving customer", e);
        }
    }
}
