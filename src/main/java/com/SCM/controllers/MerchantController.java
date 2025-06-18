package com.SCM.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.dao.LocationDetails;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.helpers.EmailAlreadyExistsException;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role.RoleType;
import com.SCM.service.CommonService;
import com.SCM.service.MerchantService;
import com.SCM.service.UserService;

@RestController
@RequestMapping("/api")
public class MerchantController {
	
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	MerchantService merchantService;
	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CommonService commonService;
	
	
	private static final Logger logger = LoggerFactory.getLogger(MerchantController.class);
	
	@RequestMapping(value = "/merchantPageWise", method = RequestMethod.GET)
    @ResponseBody
    public Page<?> getMerchantPageWise(Pageable pageable,
                                           @RequestParam(value = "address", required = false) String address,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate) {
        logger.info("Received request to fetch merchant page-wise.");
        logger.debug("Received parameters: name={}, address={}, startDate={}, endDate={}", name, address, startDate, endDate);
        
        User user = commonService.getLoggedInUser();
        
        return merchantService.getMerchant(pageable,user);
    }
	  
	@RequestMapping(value = "/merchantUserPageWise", method = RequestMethod.GET)
    @ResponseBody
    public Page<?> getMerchantUserPageWise(Pageable pageable,
                                           @RequestParam(value = "address", required = false) String address,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate) {
        logger.info("Received request to fetch merchant user page-wise.");
        logger.debug("Received parameters: name={}, address={}, startDate={}, endDate={}", name, address, startDate, endDate);
        
        User user = commonService.getLoggedInUser();
               
        return merchantService.getMerchantUser(user,pageable);
    }
	
	@PostMapping("/merchant")
    public ResponseEntity<Merchant> saveMerchant(@RequestBody Merchant merchant, @RequestParam(value = "id", required = false) Long id,
    		@RequestParam(value = "businessID", required = false) Long businessID,
    		@RequestParam(value = "regionID", required = false) Long regionID) {
        logger.info("Received request to save merchant with ID: {}", id);
        if (merchant == null) {
            logger.error("Invalid input received. Request body is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id != null) {
            merchant.setId(id);
            logger.info("Received merchant ID as a parameter: {}", id);
        }

        Merchant savedMerchant = merchantService.saveMerchant(merchant,businessID,regionID);
        if (savedMerchant != null) {
            logger.info("Merchant saved successfully with ID: {}", savedMerchant.getId());
            return new ResponseEntity<>(savedMerchant, HttpStatus.CREATED);
        } else {
            logger.error("Failed to save business.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	/*@PostMapping("/merchant/user")
    public ResponseEntity<User> saveMerchantUser(@RequestBody User user, @RequestParam(value = "id", required = false) Long id,
    		@RequestParam(value = "businessID", required = false) Long businessID,
    		@RequestParam(value = "merchantID", required = false) Long merchantID) {
        logger.info("Received request to save merchant user with ID: {}", id);
        if (user == null) {
            logger.error("Invalid input received. Request body is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id != null) {
            user.setId(id);
            logger.info("Received merchant ID as a parameter: {}", id);
        }

        User savedUser = merchantService.saveMerchantUser(user,businessID,merchantID);
        if (savedUser != null) {
            logger.info("Merchant saved successfully with ID: {}", savedUser.getId());
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } else {
            logger.error("Failed to save business.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
	
	/*@PostMapping("/merchant/user")
    public ResponseEntity<?> saveMerchantUser(@RequestBody User user, @RequestParam(value = "id", required = false) Long id,
                                              @RequestParam(value = "businessID", required = false) Long businessID,
                                              @RequestParam(value = "merchantID", required = false) Long merchantID) {
        logger.info("Received request to save merchant user with ID: {}", id);
        if (user == null) {
            logger.error("Invalid input received. Request body is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id != null) {
            user.setId(id);
            logger.info("Received merchant ID as a parameter: {}", id);
        }

        try {
            User savedUser = merchantService.saveMerchantUser(user, businessID, merchantID);
            logger.info("Merchant saved successfully with ID: {}", savedUser.getId());
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (EmailAlreadyExistsException e) {
            logger.error("Failed to save business: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/ 
	
	
	@PostMapping("/merchant/user")
	public ResponseEntity<?> saveMerchantUser(@RequestBody User user, @RequestParam(value = "id", required = false) Long id,
	        @RequestParam(value = "businessID", required = false) Long businessID,
	        @RequestParam(value = "merchantID", required = false) Long merchantID) {
	    logger.info("Received request to save merchant user with ID: {}", id);
	     
	    if (user == null) {
	        logger.error("Invalid input received. Request body is null.");
	        return ResponseEntity.badRequest().body("Invalid input received. Request body is null.");
	    }

	    if (userRepository.existsByEmail(user.getEmail())) {
	        logger.error("Email {} already exists.", user.getEmail());
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
	    }

	    if (id != null) {
	        user.setId(id);
	        logger.info("Received merchant ID as a parameter: {}", id);
	    }

	    User savedUser = merchantService.saveMerchantUser(user, businessID, merchantID);
	    if (savedUser != null) {
	        logger.info("Merchant saved successfully with ID: {}", savedUser.getId());
	        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	    } else {
	        logger.error("Failed to save business.");
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	@RequestMapping(value="/merchant/search", method = RequestMethod.GET)
   	public List<Merchant> searchMerchant(@RequestParam(value = "name") String name){
   		List<Merchant> merchant = null;
   		merchant = merchantRepository.findByName(name);
   		return merchant;
   	}
	
	@PostMapping("/merchant/search/store")
    public ResponseEntity<List<Merchant>> searchMerchantsByLocation(@RequestBody LocationDetails locationDetails) {
        // Process the received location details
        System.out.println("Received location details:");
        System.out.println("Country: " + locationDetails.getCountry());
        System.out.println("State: " + locationDetails.getState());
        System.out.println("City: " + locationDetails.getCity());

     // Search for merchants based on the provided location details
        List<Merchant> merchants = merchantRepository.findMerchantsByLocation(locationDetails.getCountry(),
        		locationDetails.getState(),
        		locationDetails.getCity());
        
        
        merchants.stream().forEach((itr) -> System.out.println(itr.getShortLink()));

        // Retrieve users associated with the found merchants
//        List<User> users = new ArrayList<>();
//        for (Merchant merchant : merchants) {
//            List<User> usersForMerchant = userRepository.findUsersByMerchant(merchant);
//            users.addAll(usersForMerchant);
//        }

        // Return a success response
        //return ResponseEntity.ok().build();
        
        return ResponseEntity.ok().body(merchants);
    }
	
	
	 @RequestMapping(value = "/merchant", method = RequestMethod.GET)
	    public ResponseEntity<?> getMerchantById(@RequestParam Long id) {
	        logger.info("Received request to fetch business with ID: {}", id);
	        try {
	            Optional<Merchant> merchant = merchantService.findByMerchantId(id);
	            if (merchant.isPresent()) {
	                logger.info("Merchant found with ID: {}", id);
	                return ResponseEntity.ok(merchant.get());
	            } else {
	                logger.warn("No merchant found with ID: {}", id);
	                return ResponseEntity.notFound().build();
	            }
	        } catch (Exception e) {
	            logger.error("Error fetching merchant data: {}", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching merchant data");
	        }
	    }

}
