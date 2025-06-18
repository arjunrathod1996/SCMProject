package com.SCM.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.SCM.cartItem.CartItem;
import com.SCM.cartItem.CartItem.CartItemStatus;
import com.SCM.cartItem.CartItemRepository;
import com.SCM.cartItem.CartService;
import com.SCM.dao.MerchantUserDTO;
import com.SCM.dtoForJWT.JWTAuthFilter;
import com.SCM.entities.Business;
import com.SCM.entities.Customer;
import com.SCM.entities.Summary;
import com.SCM.entities.User;
import com.SCM.faq.FAQ;
import com.SCM.faq.FAQList;
import com.SCM.faq.FAQListRepository;
import com.SCM.faq.FAQRepository;
import com.SCM.photo.Photo;
import com.SCM.photo.PhotoMerchant;
import com.SCM.photo.PhotoMerchantRepository;
import com.SCM.photo.PhotoRepository;
import com.SCM.repository.BusinessRepository;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.service.CommonService;
import com.SCM.service.MerchantService;
import com.SCM.service.MyViewService;

import lombok.CustomLog;



@Controller
@RequestMapping(value = "/my_app")
public class MyAppAccess {
	
	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	MerchantService merchantService;
	@Autowired
	CommonService commonService;
	@Autowired
	PhotoRepository photoRepository;
	@Autowired
	PhotoMerchantRepository photoMerchantRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	CartItemRepository cartItemRepository;
	@Autowired
	CartService cartService;
	@Autowired
	MyViewService myViewService;
	@Autowired
	FAQListRepository faqListRepository;
	@Autowired
	FAQRepository faqRepository;
	
	 private static final Logger logger = LoggerFactory.getLogger(MyAppAccess.class);

	 @GetMapping("/access")
	 public String getMyAppAccess(Authentication authentication, Model model) {
	     User user = commonService.getLoggedInUser();
	     Customer customer = null;
	     
	     if (user != null) {
	         if (user.getPhoneNumber() != null) {
	             // If the user is logged in with a phone number
	        	 logger.info("Phone Number >>>>>>>>>>> 1 {}: ", user.getPhoneNumber());
	             customer = customerRepository.findByPhoneNumber(user.getPhoneNumber());
	         } else if (user.getEmail() != null) {
	             // If the user is logged in with an email
	        	 logger.info("Phone Number >>>>>>>>>>> 2 {}: ", user.getPhoneNumber());
	             customer = customerRepository.findByEmail_(user.getEmail());
	         }
	     }
	     
	     Object customerScore = myViewService.getProfileScore(customer);
	     
	     model.addAttribute("profileScore", customerScore);
	     model.addAttribute("customer", customer);
	     return "user/my_app";
	 }

	
	@GetMapping("/login")
    public String getMyAppAccessLogin(Authentication authentication,Model model) {
        return"user/otplogin";
    }
	
	
	
	@GetMapping("/access/{shortLink}")
    public String accessPage(@PathVariable String shortLink, Model model) {
		User user = commonService.getLoggedInUser();
//		List<User> merchants =  merchantService.getMerchants(shortLink);
//		
//		List<Merchant> merchant = merchantRepository.findByShortLink_(shortLink);
//		Merchant mercnt = merchant.get(0);
//		Business business = mercnt.getBusiness();
//		
//		List<Summary> allowedFeatures = myViewService.initAllowedFeature(commonService.getMainOutlet(business));
//
//		model.addAttribute("allowedFeatures", allowedFeatures);
//		model.addAttribute("config",business.getConfig());
//		
//		model.addAttribute("merchnats",merchants);
		
		
		List<User> merchants = merchantService.getMerchants(shortLink);

		List<Merchant> merchant = merchantRepository.findByShortLink_(shortLink);
		Merchant mercnt = merchant.get(0);
		Business business = mercnt.getBusiness();

		List<Summary> allowedFeatures = myViewService.initAllowedFeature(commonService.getMainOutlet(business));

		model.addAttribute("allowedFeatures", allowedFeatures);
		model.addAttribute("config", business.getConfig());

		// Filter out users with null or empty staff links
		List<User> filteredMerchants = new ArrayList<>();
		for (User use : merchants) {
		    if (use.getStaffLink() != null && !use.getStaffLink().isEmpty()) {
		        filteredMerchants.add(use);
		    }
		}

		model.addAttribute("merchnats", filteredMerchants);

		
        return "user/my_app_access_page"; // Assuming the HTML template name is "access_page.html"
    }
	
	
	

	
	
//	@GetMapping("/access/faq/{shortLink}")
//    public String accessPageFaq(@PathVariable String shortLink, Model model) {
//		User user = commonService.getLoggedInUser();
//		List<User> merchants =  merchantService.getMerchants(shortLink);
//		List<Merchant> merchant = merchantRepository.findByShortLink_(shortLink);
//		Merchant mercnt = merchant.get(0);
//		Business business = mercnt.getBusiness();
//		List<FAQList> faqLists = faqListRepository.findByBusinessIdOrderByOrderDesc(business.getId());
//		List<FAQTopic> faqTopics = new ArrayList<>();
//		for (FAQList faqList : faqLists) {
//			List<FAQ> faqContents = faqRepository.findByFaqListIdOrderByOrder(faqList.getId());
//			FAQTopic faqTopic = new FAQTopic();
//			faqTopic.setId(faqList.getId());
//			faqTopic.setTopic(faqList.getTopic());
//			faqTopic.setQuestionsAndAnswers(faqContents);
//			faqTopics.add(faqTopic);
//		}
//		model.addAttribute("faq", faqTopics);
//        return "user/my_app_faq"; // Assuming the HTML template name is "access_page.html"
//    }
	
	
	@GetMapping("/access/faq/{shortLink}")
	public String accessPageFaq(@PathVariable String shortLink, Model model) {
	    User user = commonService.getLoggedInUser();
	    List<User> merchants = merchantService.getMerchants(shortLink);
	    List<Merchant> merchant = merchantRepository.findByShortLink_(shortLink);
	    Merchant mercnt = merchant.get(0);
	    Business business = mercnt.getBusiness();
	    List<FAQList> faqLists = faqListRepository.findByBusinessIdOrderByOrderDesc(business.getId());
	    List<FAQTopic> faqTopics = new ArrayList<>();
	    for (FAQList faqList : faqLists) {
	        List<FAQ> faqContents = faqRepository.findByFaqListIdOrderByOrder(faqList.getId());
	        // Check if FAQ list is empty, if not, add it to faqTopics
	        if (!faqContents.isEmpty()) {
	            FAQTopic faqTopic = new FAQTopic();
	            faqTopic.setId(faqList.getId());
	            faqTopic.setTopic(faqList.getTopic());
	            faqTopic.setQuestionsAndAnswers(faqContents);
	            faqTopics.add(faqTopic);
	        }
	    }
	    model.addAttribute("faq", faqTopics);
	    return "user/my_app_faq"; // Assuming the HTML template name is "access_page.html"
	}

	
	@GetMapping("/access/{shortLink}/{staffLink}")
	public String accessPageItems(@PathVariable String shortLink, @PathVariable String staffLink,
	                              @RequestParam(required = false) String search, Model model) {
	    try {
	        Merchant merchant = merchantRepository.findByShortLink(shortLink);
	        User user = userRepository.findByStaffLink(staffLink);

	        if (merchant == null || user == null) {
	            logger.error("Merchant or user not found");
	            return "error";
	        }

	        List<PhotoMerchant> photos = photoMerchantRepository.findByMerchantIdAndUserId(merchant.getId(), user.getId());
	        

	        if (search != null && !search.isEmpty()) {
	            photos = photos.stream()
	                           .filter(photo -> photo.getPhotoExtras().getTitle().toLowerCase().contains(search.toLowerCase()))
	                           .collect(Collectors.toList());
	        }

	        Map<PhotoMerchant.PhotoType, List<PhotoMerchant>> groupedPhotos = photos.stream()
	            .collect(Collectors.groupingBy(PhotoMerchant::getType));

	       /* User loggedInUser = commonService.getLoggedInUser();
	        Customer customer = customerRepository.findByEmail(loggedInUser.getEmail())
	                .orElseGet(() -> customerRepository.findByMobileNumber(loggedInUser.getPhoneNumber())
	                        .orElseThrow(() -> new RuntimeException("Customer not found")));*/
	        
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
//	                List<Customer> customers = customerRepository.findByEmail(user.getEmail());
//	                if (!customers.isEmpty()) {
//	                    // Assuming there is only one customer per email
//	                    customer = customers.get(0);
//	                }
	            	customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
	            }
	        }


	        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);

	        Map<Long, String> photoStatusMap = new HashMap<>();
	        for (CartItem cartItem : cartItems) {
	            photoStatusMap.put(cartItem.getPhotoMerchant().getId(), cartItem.getStatus() != null ? cartItem.getStatus().name() : "NONE");
	        }

//	        Map<Long, Integer> photoIdToQuantityMap = new HashMap<>();
//	        for (CartItem cartItem : cartItems) {
//	            photoIdToQuantityMap.put(cartItem.getPhotoMerchant().getId(), cartItem.getQuantity() && !cartItem.isDeleted());
//	        }
	        
	        Map<Long, Integer> photoIdToQuantityMap = new HashMap<>();
	        for (CartItem cartItem : cartItems) {
	            if (!cartItem.isDeleted()) {
	                photoIdToQuantityMap.put(cartItem.getPhotoMerchant().getId(), cartItem.getQuantity());
	            }
	        }

	        
	        Map<Long, Integer> photoIdToRatingMap = new HashMap<>();
	        for (CartItem cartItem : cartItems) {
	            if (cartItem.getStatus() == CartItem.CartItemStatus.COMPLETED && cartItem.getRating() != null) {
	                photoIdToRatingMap.put(cartItem.getPhotoMerchant().getId(), cartItem.getRating());
	            }
	        }
	        
	       
	        Map<Long, Double> photoIdToAverageRatingMap = new HashMap<>();

	        for (PhotoMerchant photo : photos) {
	            Double averageRating = cartItemRepository.findAverageRatingByMerchantIdAndUserIdAndPhotoId(
	                    CartItemStatus.PENDING, merchant.getId(), user.getId(), photo.getId());
	            photoIdToAverageRatingMap.put(photo.getId(), averageRating);
	        }



	        Integer totalQuantity = cartService.getTotalQuantityByUserIdAndMerchantIdAndCustomerId(CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId());
	        
	        // Fetch the average rating
	       // Double averageRating = cartItemRepository.findAverageRatingByMerchantIdAndUserId(CartItem.CartItemStatus.COMPLETED ,merchant.getId(), user.getId());
	        
	        
	        List<Object[]> averageRatings = cartItemRepository.findAverageRatingByPhotoMerchantId();
	        Map<Long, Double> photoMerchantIdToAverageRatingMap = new HashMap<>();

	        for (Object[] result : averageRatings) {
	            Long photoMerchantId = (Long) result[0];
	            Double averageRating = (Double) result[1];
	            photoMerchantIdToAverageRatingMap.put(photoMerchantId, averageRating);
	        }


	        model.addAttribute("photoIdToQuantityMap", photoIdToQuantityMap);
	        model.addAttribute("totalQuantity", totalQuantity);
	        model.addAttribute("photoStatusMap", photoStatusMap);
	        model.addAttribute("cartItems", cartItems);
	        model.addAttribute("groupedPhotos", groupedPhotos);
	        model.addAttribute("shortLink", shortLink);
	        model.addAttribute("staffLink", staffLink);
	        model.addAttribute("search", search);
	        model.addAttribute("customer", customer);
	        model.addAttribute("photoIdToRatingMap", photoIdToRatingMap);
	        model.addAttribute("flatPhotos", photos); // Add flattened photos list to model
	        model.addAttribute("averageRating", averageRatings); // Add average rating to model
	       // model.addAttribute("photoIdToAverageRatingMap", photoIdToAverageRatingMap);
	        
	        model.addAttribute("photoMerchantIdToAverageRatingMap", photoMerchantIdToAverageRatingMap);

	        logger.debug("Total quantity: {}", totalQuantity);

	        return "user/my_app_items";
	    } catch (Exception e) {
	        logger.error("Error accessing page items: {}", e.getMessage(), e);
	        return "error";
	    }
	}

	
	@GetMapping("/access/{shortLink}/{staffLink}/bill")
	public String accessPageBill(@PathVariable String shortLink, @PathVariable String staffLink,
	                              @RequestParam(required = false) String search, Model model) {
	    try {
	        Merchant merchant = merchantRepository.findByShortLink(shortLink);
	        User user = userRepository.findByStaffLink(staffLink);

	        if (merchant == null || user == null) {
	            logger.error("Merchant or user not found");
	            return "error";
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
//	                List<Customer> customers = customerRepository.findByEmail(user.getEmail());
//	                if (!customers.isEmpty()) {
//	                    // Assuming there is only one customer per email
//	                    customer = customers.get(0);
//	                }
	            	customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
	            }
	        }

	        logger.debug("Customer: {}", customer);

	        List<CartItem> cartItem = cartItemRepository.getCartItemByUserIdAndMerchantIdAndCustomerId(CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId());
	        Integer totalQuantity = cartService.getTotalQuantityByUserIdAndMerchantIdAndCustomerId(CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId());
	        model.addAttribute("totalQuantity", totalQuantity);
	        logger.debug("Cart items: {}", cartItem);

	        model.addAttribute("cartItem", cartItem);

	        Integer totalAmount = cartService.getTotalAmountByUserIdAndMerchantIdAndCustomerId(CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId());

	        model.addAttribute("totalAmount", totalAmount);
	        
	        model.addAttribute("customer",customer);

	        return "user/my_bill"; // Assuming the HTML template name is "my_app_items.html"
	    } catch (Exception e) {
	        logger.error("Error accessing page bill: {}", e.getMessage(), e);
	        return "error";
	    }
	}

	
	
	
	@GetMapping("/access/{shortLink}/{staffLink}/history")
	public String accessPageBillHistory(@PathVariable String shortLink, @PathVariable String staffLink,
	                                    @RequestParam(required = false) String search, Model model) {
	    try {
	        Merchant merchant = merchantRepository.findByShortLink(shortLink);
	        User user = userRepository.findByStaffLink(staffLink);

	        if (merchant == null || user == null) {
	            logger.error("Merchant or user not found");
	            return "error";
	        }

	        /*Customer customer = customerRepository.findByEmail(commonService.getLoggedInUser().getEmail())
	                .orElseThrow(() -> new RuntimeException("Customer not found"));*/ 
	        
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
//	                List<Customer> customers = customerRepository.findByEmail(user.getEmail());
//	                if (!customers.isEmpty()) {
//	                    // Assuming there is only one customer per email
//	                    customer = customers.get(0);
//	                }
	            	customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
	            }
	        }

	        logger.debug("Customer: {}", customer);

	        List<CartItem> cartItem = cartItemRepository.getCartItemByUserIdAndMerchantIdAndCustomerId(CartItem.CartItemStatus.COMPLETED, user.getId(), merchant.getId(), customer.getId());

	        logger.debug("Cart items: {}", cartItem);

	        model.addAttribute("cartItem", cartItem);

	        Integer totalAmount = cartService.getTotalAmountByUserIdAndMerchantIdAndCustomerId(CartItem.CartItemStatus.COMPLETED, user.getId(), merchant.getId(), customer.getId());

	        model.addAttribute("totalAmount", totalAmount);
	        
	        model.addAttribute("customer", customer);
	        
	        Integer totalQuantity = cartService.getTotalQuantityByUserIdAndMerchantIdAndCustomerId(CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId());
	        model.addAttribute("totalQuantity", totalQuantity);
	        

	        return "user/my_bill_history"; // Assuming the HTML template name is "my_app_items.html"
	    } catch (Exception e) {
	        logger.error("Error accessing page bill history: {}", e.getMessage(), e);
	        return "error";
	    }
	}

	
	
	@GetMapping("/access/{shortLink}/{staffLink}/{photoId}/review")
	public String accessPageReview(@PathVariable String shortLink, @PathVariable String staffLink,
	                               @PathVariable Long photoId, @RequestParam(required = false) String search, Model model) {
	    try {
	        Merchant merchant = merchantRepository.findByShortLink(shortLink);
	        User user = userRepository.findByStaffLink(staffLink);

	        if (merchant == null || user == null) {
	            logger.error("Merchant or user not found");
	            return "error";
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

	        if (customer == null) {
	            logger.warn("Customer not found for loggedInUser={}", loggedInUser);
	            return "error";
	        }

	        List<CartItem> completedCartItemsWithRating = cartItemRepository.getCartItemByUserIdAndMerchantIdAndCustomerIdAndPhotoMerchantId(
	                CartItemStatus.COMPLETED, user.getId(), merchant.getId(), customer.getId(), photoId);
	        
	       // List<CartItem> completedCartItemsWithRating = cartService.getCartItemsByMultipleStatuses(user.getId(), merchant.getId(), customer.getId(), photoId);

	        // Grouping cart items by PhotoType
	        Map<PhotoMerchant.PhotoType, List<CartItem>> groupedCartItems = completedCartItemsWithRating.stream()
	                .collect(Collectors.groupingBy(cartItem -> cartItem.getPhotoMerchant().getType()));

	        Integer totalQuantity = cartService.getTotalQuantityByUserIdAndMerchantIdAndCustomerId(CartItem.CartItemStatus.PENDING, user.getId(), merchant.getId(), customer.getId());
	        model.addAttribute("totalQuantity", totalQuantity);
	        model.addAttribute("customer", customer);
	        model.addAttribute("completedCartItemsWithRating", completedCartItemsWithRating);
	        model.addAttribute("groupedCartItems", groupedCartItems);  // Add grouped items to the model
	        model.addAttribute("totalQuantity", totalQuantity);

	        logger.debug("Total quantity: {}", totalQuantity);
	        return "user/my_review";
	    } catch (Exception e) {
	        logger.error("Error accessing page review: {}", e.getMessage(), e);
	        return "error";
	    }
	}




	
	@PostMapping("/access/{shortLink}/{staffLink}/add-to-cart")
	@ResponseBody
	public ResponseEntity<String> addToCart(@PathVariable String shortLink, @PathVariable String staffLink,
	                                        @RequestParam Long photoMerchantId, @RequestParam int quantity,Model model) {
	   
		User user = commonService.getLoggedInUser();
		Customer customer = customerRepository.findByEmail_(user.getEmail());
		CartItem cart = cartItemRepository.findById(customer.getId()).get();
		model.addAttribute("cart",cart);
	    return new ResponseEntity<>("Item added to cart", HttpStatus.OK);
	}
	
	
	public List<FAQTopic> getAllFAQTopicsByBusiness(Long businessId) {

		List<FAQList> faqLists = faqListRepository.findByBusinessIdOrderByOrderDesc(businessId);

		List<FAQTopic> faqTopics = new ArrayList<>();

		for (FAQList faqList : faqLists) {

			List<FAQ> faqContents = faqRepository.findByFaqListId(faqList.getId());

			FAQTopic faqTopic = new FAQTopic();

			faqTopic.setId(faqList.getId());
			faqTopic.setTopic(faqTopic.getTopic());
			faqTopic.setQuestionsAndAnswers(faqContents);
			faqTopics.add(faqTopic);
		}

		return faqTopics;

	}

	public static class FAQTopic {
		Long id;
		String topic;
		List<FAQ> questionsAndAnswers;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public List<FAQ> getQuestionsAndAnswers() {
			return questionsAndAnswers;
		}

		public void setQuestionsAndAnswers(List<FAQ> questionsAndAnswers) {
			this.questionsAndAnswers = questionsAndAnswers;
		}

	}
	
	@GetMapping("/access/user/profile")
	public String accessPageProfile(@RequestParam(value = "shortLink", required = false) String shortLink, Model model) {
	    User user = commonService.getLoggedInUser();
	    if (user == null) {
	        logger.error("User not found");
	        return "redirect:/login"; // Redirect to login if the user is not found
	    }

	    Customer customer = null;
	    if (user.getPhoneNumber() != null) {
	        customer = customerRepository.findByPhoneNumber(user.getPhoneNumber());
	    } else if (user.getEmail() != null) {
	        customer = customerRepository.findByEmail_(user.getEmail());
	    }

	    if (customer == null) {
	        logger.error("Customer not found");
	        return "error/404"; // Show a 404 error page if the customer is not found
	    }

	    model.addAttribute("customer", customer);
	    return "user/my_app_profile"; // Assuming the HTML template name is "user/my_app_profile.html"
	}
	
	@GetMapping("/access/user/profile/{shortLink}")
	public String accessPageProfile(@RequestParam(value = "shortLink", required = false) String shortLink, 
	                                @PathVariable(value = "shortLink", required = false) String pathShortLink,
	                                Model model) {
	    try {
	        if (shortLink != null && !shortLink.isEmpty()) {
	            // Use the shortLink query parameter to retrieve merchant details
	            Merchant merchant = merchantRepository.findByShortLink(shortLink);
	            logger.error("Merchant found for shortLink (query parameter): {}", shortLink);
	            if (merchant == null) {
	                logger.error("Merchant not found for shortLink (query parameter): {}", shortLink);
	                return "error/404"; // Show a 404 error page if the merchant is not found for the shortLink
	            }
	            model.addAttribute("merchant", merchant);
	        } else if (pathShortLink != null && !pathShortLink.isEmpty()) {
	            // Use the shortLink path variable to retrieve merchant details
	            Merchant merchant = merchantRepository.findByShortLink(pathShortLink);
	            logger.error("Merchant found for shortLink (path variable): {}", pathShortLink);
	            if (merchant == null) {
	                logger.error("Merchant not found for shortLink (path variable): {}", pathShortLink);
	                return "error/404"; // Show a 404 error page if the merchant is not found for the shortLink
	            }
	            model.addAttribute("merchant", merchant);
	        } else {
	            // If neither shortLink nor pathShortLink is provided, use the logged-in user's information
	            User user = commonService.getLoggedInUser();
	            if (user == null) {
	                logger.error("User not found");
	                return "redirect:/login"; // Redirect to login if the user is not found
	            }

	            Customer customer = null;
	            if (user.getPhoneNumber() != null) {
	                customer = customerRepository.findByPhoneNumber(user.getPhoneNumber());
	            } else if (user.getEmail() != null) {
	                customer = customerRepository.findByEmail_(user.getEmail());
	            }

	            if (customer == null) {
	                logger.error("Customer not found");
	                return "error/404"; // Show a 404 error page if the customer is not found
	            }

	            model.addAttribute("customer", customer);
	            
	        }
	        return "user/my_app_profile"; // Assuming the HTML template name is "user/my_app_profile.html"
	    } catch (Exception e) {
	        logger.error("An error occurred while processing the profile page request", e);
	        return "error/500"; // Show a generic 500 error page for any unexpected exceptions
	    }
	}





}
