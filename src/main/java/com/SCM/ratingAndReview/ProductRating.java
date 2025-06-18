package com.SCM.ratingAndReview;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.cartItem.BillingSummary;
import com.SCM.cartItem.CartItem;
import com.SCM.cartItem.CartItem.CartItemStatus;
import com.SCM.cartItem.CartItemRepository;
import com.SCM.cartItem.CartService;
import com.SCM.controllers.Merchant;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.photo.PhotoMerchant;
import com.SCM.photo.PhotoMerchantRepository;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.service.CommonService;

@RestController
@RequestMapping("/api")
public class ProductRating {
	
	@Autowired
	PhotoMerchantRepository photoMerchantRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	CommonService commonService;
	@Autowired
	CartItemRepository cartItemRepository;
	@Autowired
	CartService cartService;
	
	private static final Logger logger = LoggerFactory.getLogger(ProductRating.class);
	

	@PostMapping("/rating")
	public ResponseEntity<String> rating(@RequestParam(value = "photoId", required = true) Long photoId,
	                                     @RequestParam(value = "shortLink", required = true) String shortLink,
	                                     @RequestParam(value = "staffLink", required = true) String staffLink,
	                                     @RequestParam(value = "selectedRating", required = true) Integer selectedRating,
	                                     @RequestParam (value = "reviewText", required = false)String reviewText) {
		
		
		System.out.println("Review Text: " + reviewText);
	    try {
	        logger.info("Received rating request: photoId={}, shortLink={}, staffLink={}, selectedRating={}", 
	                     photoId, shortLink, staffLink, selectedRating);

	        // Fetch the merchant and user based on provided links
	        Merchant merchant = merchantRepository.findByShortLink(shortLink);
	        User user = userRepository.findByStaffLink(staffLink);
	        
	       

	        // Validate the merchant and user
	        if (merchant == null || user == null) {
	            logger.warn("Invalid merchant or user details: merchant={}, user={}", merchant, user);
	            return ResponseEntity.badRequest().body("Invalid merchant or user details");
	        }

	        User loggedInUser = commonService.getLoggedInUser();
	        Customer customer = null;

	        if (loggedInUser != null) {
	            if (loggedInUser.getPhoneNumber() != null) {
	                List<Customer> customers = customerRepository.findByMobileNumber(loggedInUser.getPhoneNumber());
	                if (!customers.isEmpty()) {
	                    customer = customers.get(0);
	                }
	            } else if (loggedInUser.getEmail() != null) {
	                customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
	            }
	        }

	        // Validate the customer
	        if (customer == null) {
	            logger.warn("Customer not found for loggedInUser={}", loggedInUser);
	            return ResponseEntity.badRequest().body("Customer not found");
	        }

	        List<CartItem> items = cartItemRepository.getCartItemByUserIdAndMerchantIdAndCustomerIdAndPhotoMerchantId(
	                CartItemStatus.COMPLETED, user.getId(), merchant.getId(), customer.getId(), photoId);
	        
	     //   List<CartItem> items = cartService.getCartItemsByMultipleStatuses(user.getId(), merchant.getId(), customer.getId(), photoId);

	        if (items.isEmpty()) {
	            logger.warn("No completed cart items found for user={}, merchant={}, customer={}", 
	                         user.getId(), merchant.getId(), customer.getId());
	            return ResponseEntity.badRequest().body("No completed cart items found");
	        }

	        for (CartItem item : items) {
	            logger.info("Processing cart item: id={}", item.getId());
	            if (item.getRating() != null) {
	                item.setRevise(item.getRevise() == null ? 1 : item.getRevise() + 1);
	            }
	            item.setRating(selectedRating);
	            item.setReviewText(reviewText);
	            cartItemRepository.save(item);  // Save the updated cart item
	        }

	        // Return success response
	        logger.info("Successfully processed rating for photoId={}", photoId);
	        return ResponseEntity.ok("Successfully Submitted");
	    } catch (Exception e) {
	        logger.error("Error processing rating request", e);
	        return ResponseEntity.status(500).body("Internal server error");
	    }
	}


   
}
