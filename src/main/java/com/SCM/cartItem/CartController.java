package com.SCM.cartItem;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.cartItem.CartItem.CartItemStatus;
import com.SCM.controllers.BusinessController;
import com.SCM.controllers.Merchant;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.insight.CartBillSummaryInsight.CustomerRankingStat;
import com.SCM.insight.CartBillSummaryInsightService;
import com.SCM.photo.PhotoMerchantRepository;
import com.SCM.relation.CustomerRelation;
import com.SCM.relation.CustomerRelation.AquisitionMode;
import com.SCM.relation.CustomerRelation.Source;
import com.SCM.relation.CustomerRelationRepository;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.service.CommonService;

@RestController
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartItemRepository cartItemRepository; // Assuming you have a CartItemRepository
    
    @Autowired
    PhotoMerchantRepository photoMerchantRepository;
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    CommonService commonService;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    CartService cartService;
    
    @Autowired
    MerchantRepository merchantRepository;
    
    @Autowired
    BillingSummaryRepository billingSummaryRepository;
    
    @Autowired
    CustomerRelationRepository customerRelationRepository;
    
    @Autowired
    CartBillSummaryInsightService billSummaryInsightService;
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

    @PostMapping("/access/{shortLink}/{staffLink}/add-to-cart")
    @ResponseBody
    public ResponseEntity<String> addToCart(@PathVariable String shortLink, @PathVariable String staffLink,
                                            @RequestParam Long photoMerchantId, @RequestParam int quantity) {
        try {
            System.out.println("addToCart endpoint - shortLink: " + shortLink + ", staffLink: " + staffLink + ", photoMerchantId: " + photoMerchantId + ", quantity: " + quantity);

            User user = userRepository.findByStaffLink(staffLink);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Merchant merchant = merchantRepository.findByShortLink(shortLink);
            if (merchant == null) {
                return new ResponseEntity<>("Merchant not found", HttpStatus.NOT_FOUND);
            }

            cartService.addToCart(user.getId(), photoMerchantId, quantity);

            return new ResponseEntity<>("Item added to cart", HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error occurred while adding to cart: " + e.getMessage());
            return new ResponseEntity<>("Error occurred while adding item to cart", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



//    @PostMapping("/access/{shortLink}/{staffLink}/bill")
//    public ResponseEntity<String> billItems(@PathVariable String shortLink, @PathVariable String staffLink) {
//    	
//    	
//    	Merchant merchant = merchantRepository.findByShortLink(shortLink);
//	    User user = userRepository.findByStaffLink(staffLink);
//
//	    if (merchant == null || user == null) {
//	        throw new RuntimeException("Please Check...");
//	    }
//    	
//        // Retrieve the customer and their cart items
//        Customer customer = customerRepository.findByEmail(commonService.getLoggedInUser().getEmail()).get();
//        if (customer == null) {
//            return ResponseEntity.badRequest().body("Customer not found");
//        }
//        
//        
//        
//        // Update the status of cart items to COMPLETED
//        List<CartItem> cartItems = cartItemRepository.getCartItemByUserIdAndMerchantIdAndCustomerId(CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId());
//        
//     // Calculate the total amount
//        double totalAmount = 0.0;
//        for (CartItem cartItem : cartItems) {
//            totalAmount += cartItem.getPrice() * cartItem.getQuantity();
//        }
//
//        // Create and save the billing summary
//        BillingSummary billingSummary = new BillingSummary();
//        billingSummary.setTotalAmount(totalAmount);
//        billingSummary.setUser(user);
//        billingSummary.setMerchant(merchant);
//
//        billingSummaryRepository.save(billingSummary);
//        
//        for (CartItem cartItem : cartItems) {
//            cartItem.setStatus(CartItem.CartItemStatus.COMPLETED);
//        }
//        
//        for (CartItem cartItem : cartItems) {
//            System.out.println(cartItem.toString());
//        }
//        
//        cartItemRepository.saveAll(cartItems);
//        
//        return ResponseEntity.ok("Billing process completed successfully");
//    }
    
//    @PostMapping("/access/{shortLink}/{staffLink}/bill")
//    public ResponseEntity<String> billItems(@PathVariable String shortLink, @PathVariable String staffLink) {
//        Merchant merchant = merchantRepository.findByShortLink(shortLink);
//        User user = userRepository.findByStaffLink(staffLink);
//
//        if (merchant == null || user == null) {
//            return ResponseEntity.badRequest().body("Invalid merchant or user details");
//        }
//
//        Customer customer = customerRepository.findByEmail(commonService.getLoggedInUser().getEmail()).orElse(null);
//        if (customer == null) {
//            return ResponseEntity.badRequest().body("Customer not found");
//        }
//
//        List<CartItem> cartItems = cartItemRepository.getCartItemByUserIdAndMerchantIdAndCustomerId(
//            CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId()
//        );
//
//        // Calculate the total amount
//        double totalAmount = 0.0;
//        for (CartItem cartItem : cartItems) {
//            totalAmount += cartItem.getPrice() * cartItem.getQuantity();
//        }
//
//        // Check if the total amount is zero
//        if (totalAmount == 0.0) {
//            return ResponseEntity.badRequest().body("Total amount cannot be zero");
//        }
//
//        // Create and save the billing summary
//        BillingSummary billingSummary = new BillingSummary();
//        billingSummary.setTotalAmount(totalAmount);
//        billingSummary.setUser(user);
//        billingSummary.setMerchant(merchant);
//        billingSummaryRepository.save(billingSummary);
//
//        // Update the status of cart items to COMPLETED
//        for (CartItem cartItem : cartItems) {
//            cartItem.setStatus(CartItem.CartItemStatus.COMPLETED);
//        }
//        cartItemRepository.saveAll(cartItems);
//
//        return ResponseEntity.ok("Billing process completed successfully");
//    }
    
//    @PostMapping("/access/{shortLink}/{staffLink}/bill")
//    public ResponseEntity<String> billItems(@PathVariable String shortLink, @PathVariable String staffLink) {
//        // Fetch the merchant and user based on provided links
//        Merchant merchant = merchantRepository.findByShortLink(shortLink);
//        User user = userRepository.findByStaffLink(staffLink);
//
//        // Validate the merchant and user
//        if (merchant == null || user == null) {
//            return ResponseEntity.badRequest().body("Invalid merchant or user details");
//        }
//
//        // Get the logged-in customer's email and fetch the customer
//        /*String loggedInUserEmail = commonService.getLoggedInUser().getEmail();
//        Customer customer = customerRepository.findByEmail(loggedInUserEmail).orElse(null);*/
//        
//        
//        User loggedInUser = commonService.getLoggedInUser();
//        Customer customer = null;
//
//        if (loggedInUser != null) {
//            if (loggedInUser.getPhoneNumber() != null) {
//                // If the user is logged in with a phone number
//                List<Customer> customers = customerRepository.findByMobileNumber(loggedInUser.getPhoneNumber());
//                if (!customers.isEmpty()) {
//                    // Assuming there is only one customer per phone number
//                    customer = customers.get(0);
//                }
//            } else if (loggedInUser.getEmail() != null) {
//                // If the user is logged in with an email
////                List<Customer> customers = customerRepository.findByEmail(user.getEmail());
////                if (!customers.isEmpty()) {
////                    // Assuming there is only one customer per email
////                    customer = customers.get(0);
////                }
//            	customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
//            }
//        }
//
//        // Validate the customer
//        if (customer == null) {
//            return ResponseEntity.badRequest().body("Customer not found");
//        }
//
//        // Retrieve the pending cart items for the user, merchant, and customer
//        List<CartItem> cartItems = cartItemRepository.getCartItemByUserIdAndMerchantIdAndCustomerId(
//            CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId()
//        );
//
//        // Calculate the total amount for the cart items
//        double totalAmount = cartItems.stream()
//                                      .mapToDouble(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
//                                      .sum();
//
//        // Check if the total amount is zero
//        if (totalAmount == 0.0) {
//            return ResponseEntity.badRequest().body("Total amount cannot be zero");
//        }
//
//        // Create and save the billing summary
//        createAndSaveBillingSummary(totalAmount, user, merchant);
//
//        // Update the status of cart items to COMPLETED
//        cartItems.forEach(cartItem -> cartItem.setStatus(CartItem.CartItemStatus.COMPLETED));
//        cartItemRepository.saveAll(cartItems);
//
//        return ResponseEntity.ok("Billing process completed successfully");
//    }
//
//    private void createAndSaveBillingSummary(double totalAmount, User user, Merchant merchant) {
//        BillingSummary billingSummary = new BillingSummary();
//        billingSummary.setTotalAmount(totalAmount);
//        billingSummary.setUser(user);
//        billingSummary.setMerchant(merchant);
//        billingSummaryRepository.save(billingSummary);
//    }
    
    
    @PostMapping("/access/{shortLink}/{staffLink}/bill")
    public ResponseEntity<String> billItems(@PathVariable String shortLink, @PathVariable String staffLink) {
        // Fetch the merchant and user based on provided links
        Merchant merchant = merchantRepository.findByShortLink(shortLink);
        User user = userRepository.findByStaffLink(staffLink);

        // Validate the merchant and user
        if (merchant == null || user == null) {
            return ResponseEntity.badRequest().body("Invalid merchant or user details");
        }

        // Get the logged-in customer's email and fetch the customer
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
                customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
            }
        }

        // Validate the customer
        if (customer == null) {
            return ResponseEntity.badRequest().body("Customer not found");
        }

        // Retrieve the pending cart items for the user, merchant, and customer
        List<CartItem> cartItems = cartItemRepository.getCartItemByUserIdAndMerchantIdAndCustomerId(
            CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId()
        );

        // Calculate the total amount for the cart items
        double totalAmount = cartItems.stream()
                                      .mapToDouble(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                                      .sum();

        // Check if the total amount is zero
        if (totalAmount == 0.0) {
            return ResponseEntity.badRequest().body("Total amount cannot be zero");
        }

        // Create and save the billing summary
        createAndSaveBillingSummary(totalAmount, user, merchant);

        // Update the status of cart items to COMPLETED
        cartItems.forEach(cartItem -> cartItem.setStatus(CartItem.CartItemStatus.COMPLETED));
        cartItemRepository.saveAll(cartItems);

        // Check if CustomerRelation already exists
        Optional<CustomerRelation> existingRelation = customerRelationRepository.findByMerchantAndCustomer(merchant, customer);
        if (!existingRelation.isPresent()) {
            // Create and save the CustomerRelation
            CustomerRelation customerRelation = new CustomerRelation();
            customerRelation.setMerchant(merchant);
            customerRelation.setCustomer(customer);
            customerRelation.setSource(Source.TRANSACTION);
            customerRelation.setAquisitionMode(AquisitionMode.DIRECT);
            // Set other necessary fields if required
            customerRelationRepository.save(customerRelation);
        }

        return ResponseEntity.ok("Billing process completed successfully");
    }

    private void createAndSaveBillingSummary(double totalAmount, User user, Merchant merchant) {
        BillingSummary billingSummary = new BillingSummary();
        billingSummary.setTotalAmount(totalAmount);
        billingSummary.setUser(user);
        billingSummary.setMerchant(merchant);
        billingSummaryRepository.save(billingSummary);
    }

    @RequestMapping(value = "/billPageWise", method = RequestMethod.GET)
    @ResponseBody
    public Page<?> getBusinessPageWise(Pageable pageable,
                                           @RequestParam(value = "merchantID", required = false) Long merchantID,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate,
                                           @RequestParam(value = "email", required = false) String email,
                                           @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
                                           @RequestParam(value = "status", required = false) CartItemStatus status) {
    	
    	
    	
    	
    	
    	User user = commonService.getLoggedInUser(); 
       
        return cartService.getBillSummary(pageable,user,merchantID,email,mobileNumber,startDate,endDate,status); 
    }
    
    

    @RequestMapping(value = "/customerPageWise", method = RequestMethod.GET)
    @ResponseBody
    public Page<?> getCustomerPageWise(Pageable pageable,
                                           @RequestParam(value = "merchantID", required = false) Long merchantID,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate,
                                           @RequestParam(value = "email", required = false) String email,
                                           @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
                                           @RequestParam(value = "status", required = false) CartItemStatus status) {
    	
    	
    	
    	
    	
    	User user = commonService.getLoggedInUser(); 
       
        return cartService.getCustomer(pageable, user, merchantID, email, mobileNumber, startDate, endDate, status);
    }

    
    
    @PostMapping("/removeItem_")
    public ResponseEntity<?> removeItem(@RequestBody CartItem cartItem,
    		@RequestParam(value=("itemId"), required = true) Long itemId,
    		@RequestParam(value=("shortLink"), required = true) String shortLink,
    		@RequestParam(value=("staffLink"), required = true) String staffLink) {

        // Retrieve the necessary entities
        Merchant merchant = merchantRepository.findByShortLink(shortLink);
        User user = userRepository.findByStaffLink(staffLink);

        // Ensure the required entities are present
        if (merchant == null || user == null) {
            return ResponseEntity.badRequest().body("Invalid merchant or user details");
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
              customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
          }
      	}

        // Ensure the customer is found
        if (customer == null) {
            return ResponseEntity.badRequest().body("Customer not found");
        }
        
        if(itemId != null) {
        	cartItem.setId(itemId);
        }
        
       

        // Retrieve the cart item by its ID
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItem.getId());

        // Ensure the cart item is present
        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            
            if(cartItem.getId() != null) {
            	  System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>> cartItem : " + cartItem);

                  // Check if the retrieved cart item matches the provided merchant, user, and customer
                  if (cartItem.getPhotoMerchant().getMerchant().equals(merchant) &&
                      cartItem.getPhotoMerchant().getUser().equals(user) &&
                      cartItem.getCustomer().equals(customer)) {

                      // Update the cart item status and mark it as deleted
                      cartItem.setStatus(CartItemStatus.CANCELED);
                      cartItem.setDeleted(true);
                      
                      System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

                      // Save the updated cart item
                      cartItemRepository.save(cartItem);

                     
            }
                  // Return success response
                  return ResponseEntity.ok("Successfully removed");
            
            } else {
                // Return error response if the retrieved cart item does not match the provided details
                return ResponseEntity.badRequest().body("Invalid cart item details");
            }
        } else {
            // Return error response if the cart item is not found
            return ResponseEntity.badRequest().body("CartItem not found");
        }
    }

    @PostMapping("/removeItem")
    public ResponseEntity<?> removeAndUpdateCartItem(@RequestBody CartItem cartItem,
                                                      @RequestParam(value = "itemId", required = false) Long itemId,
                                                      @RequestParam(value = "shortLink", required = false) String shortLink,
                                                      @RequestParam(value = "staffLink", required = false) String staffLink) {
        try {
            logger.info("Received request to save CartItem with ID: {}", itemId);
            
            // Check if the request body is null
            if (cartItem == null) {
                logger.error("Invalid input received. Request body is null.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Retrieve the necessary entities
            Merchant merchant = merchantRepository.findByShortLink(shortLink);
            User user = userRepository.findByStaffLink(staffLink);

            // Ensure the required entities are present
            if (merchant == null || user == null) {
                logger.error("Invalid merchant or user details. Merchant: {}, User: {}", merchant, user);
                return ResponseEntity.badRequest().body("Invalid merchant or user details");
            }

            // Retrieve the logged-in user and associated customer
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
                    customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
                }
            }

            // Ensure the customer is found
            if (customer == null) {
                logger.error("Customer not found for logged-in user: {}", loggedInUser);
                return ResponseEntity.badRequest().body("Customer not found");
            }

            // Set the ID if provided in the request
            if (itemId != null) {
                cartItem.setId(itemId);
                logger.info("Received cartItem ID as a parameter: {}", itemId);
            }

            // Save or update the cart item
            CartItem savedCartItem = cartService.saveCart(cartItem);
            if (savedCartItem != null) {
                logger.info("Cart saved successfully with ID: {}", savedCartItem.getId());
                return new ResponseEntity<>(savedCartItem, HttpStatus.CREATED);
            } else {
                logger.error("Failed to save CartItem.");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception ex) {
            logger.error("An error occurred while processing the request: {}", ex.getMessage(), ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/customer-ranking")
    public ResponseEntity<CustomerRankingStat> getCustomerRanking(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer entryCount) {
    	
    	System.out.println(" >>>>>>>>>>>>>> : " + startDate);
    	System.out.println(" >>>>>>>>>>>>>> : " + endDate);
    	System.out.println(" >>>>>>>>>>>>>> : " + entryCount);

    	User user = commonService.getLoggedInUser();    	
        CustomerRankingStat crStat = billSummaryInsightService.getCustomerRankingStat(user.getBusiness(), startDate, endDate, entryCount);
        return ResponseEntity.ok(crStat);
    }
    
    


}

