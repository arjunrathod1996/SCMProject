package com.SCM.service;

import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.SCM.controllers.Country;
import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.repository.CountryRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;

@Service
public class CommonService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	CountryRepository countryRepository;
	
	public static float LOW_BALANCE = 100f;
	
	public static Float MIN_BALANCE_REQUIRED = 100.0f;
	
	public static final String DEFAULT_COUNTRY_CALLING_CODE = "91";
	
	public static final float MIN_BALANCE = 5.0f;
	
	private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

	public User getLoggedInUser() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.isAuthenticated()) {
	        String loggedInUsername = authentication.getName();
	         
	        try {
	            // Attempt to retrieve the user by email
	            Optional<User> userByEmailOptional = userRepository.findByEmail(loggedInUsername);
	            if (userByEmailOptional.isPresent()) {
	                User userByEmail = userByEmailOptional.get();
	                logger.info("Logged-in user retrieved successfully by email: {}", loggedInUsername);
	                return userByEmail;
	            } else {
	                logger.info("No user found with email: {}", loggedInUsername);
	            }
	             
	            // Attempt to retrieve the user by phone number
	            Optional<User> userByPhoneNumberOptional = userRepository.findByPhoneNumber_(loggedInUsername);
	            if (userByPhoneNumberOptional.isPresent()) {
	                User userByPhoneNumber = userByPhoneNumberOptional.get();
	                logger.info("Logged-in user retrieved successfully by phone number: {}", loggedInUsername);
	                return userByPhoneNumber;
	            } else {
	                logger.info("No user found with phone number: {}", loggedInUsername);
	            }
	            
	            logger.error("Logged-in user not found in the database: {}", loggedInUsername);
	        } catch (Exception e) {
	            logger.error("Error occurred while retrieving logged-in user: {}", e.getMessage());
	        }
	    } else {
	        logger.error("No authenticated user found.");
	    }
	    return null;
	}


	
	public List<Merchant> getMerchants(Business business) {
	    return merchantRepository.findByBusiness(business);
	}
	
	public Country getDefaultCountry() {
		return countryRepository.findByCallingCode(DEFAULT_COUNTRY_CALLING_CODE);
	}
	
	public List<Long> getMerchantIDs(Business business){
		List<Merchant> merchants = merchantRepository.findByBusiness(business);
		List<Long> merchanIDs = new ArrayList<>();
		for(Merchant merchant : merchants) {
			merchanIDs.add(merchant.getId());
		}
		return merchanIDs.size() == 0 ? Arrays.asList(-1L) : merchanIDs;
	}
	
	public Merchant getMainOutlet(Business business) {
		List<Merchant> merchants = merchantRepository.findByBusinessAndMainOutlet(business , true);
		if(merchants.size() == 0) {
			merchants = merchantRepository.findByBusiness(business);
			return merchants.get(0);
		}
		if(merchants.size() > 1) {
			System.out.println("Incorrect");
		}
		return merchants.get(0);
	}
}
