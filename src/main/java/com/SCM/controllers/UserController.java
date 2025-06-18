package com.SCM.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.SCM.cartItem.BillingSummary;
import com.SCM.cartItem.CartItem;
import com.SCM.cartItem.CartItem.CartItemStatus;
import com.SCM.cartItem.CartItemRepository;
import com.SCM.cartItem.CartService;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.enumHelper.EnumHelper;
import com.SCM.helpers.AttributeConsts;
import com.SCM.leads.LeadMainRepository;
import com.SCM.photo.GalleryService;
import com.SCM.photo.Photo;
import com.SCM.photo.PhotoMerchant;
import com.SCM.photo.PhotoMerchantRepository;
import com.SCM.photo.PhotoRepository;
import com.SCM.repository.BusinessRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role.RoleType;
import com.SCM.service.CommonService;
import com.SCM.service.DashboardService;
import com.SCM.service.MerchantService;
import com.SCM.service.UserServiceImpl;


import jakarta.servlet.http.HttpServletResponse;


@Controller
@RequestMapping(value="/user")
public class UserController {

    @Autowired
    UserRepository repository;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    CommonService commonService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GalleryService galleryService;
    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    PhotoRepository photoRepository;
    @Autowired
    BusinessRepository businessRepository;
    @Autowired
    PhotoMerchantRepository photoMerchantRepository;
    @Autowired
    private EnumHelper enumHelper;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    MerchantService merchantService;
    @Autowired
    DashboardService dashboardService;
    @Autowired
    LeadMainRepository leadMainRepository;
 
   
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final String ACTIVITY_EXPORT_FILE_NAME = "Billing_Activity_%s.xlsx"; // Change the file extension to .xlsx
	private final String ACTIVITY_EXPORT_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  
	@GetMapping("/dashboard")    
    public String getUserDashboard(Authentication authentication, Model model) {
	

	   
        
        logger.info("Attempting to access the /dashboard endpoint.");
        User user = commonService.getLoggedInUser();
        if (user == null) {
            logger.warn("No logged-in user found.");
            throw new AccessDeniedException("Access is denied");
        }
        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
        logger.info("Logged-in user role: {}", userRole);
        if (!"ROLE_ADMIN".equals(userRole) && !"ROLE_MERCHANT_ADMIN".equals(userRole) && !"ROLE_MERCHANT_STAFF".equals(userRole)) {
            logger.warn("Access denied. User does not have ROLE_ADMIN , ROLE_MERCHANT_ADMIN, ROLE_MERCHANT_STAFF authority.");
            throw new AccessDeniedException("Access is denied");
        }
        logger.info("Access granted");
        
        int leadCount = dashboardService.getLeadCountByUserRole(user);
        model.addAttribute("leadCount", leadCount);
        
        Map<String, Integer> leadCounts = dashboardService.getLeadCountsByUserRole(user);
        
        System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : " + leadCounts);
        
        // Add the lead counts to the model
      
        
        // Other model attributes and logic
        
        return "user/user_dashboard";
    }


    @GetMapping("/dashboard/business")    
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String createBusiness() {
        logger.info("Attempting to access the /dashboard/business endpoint.");
        User user = commonService.getLoggedInUser();
        if (user == null) {
            logger.warn("No logged-in user found.");
            throw new AccessDeniedException("Access is denied");
        }
        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
        logger.info("Logged-in user role: {}", userRole);
        if (!"ROLE_ADMIN".equals(userRole)) {
            logger.warn("Access denied. User does not have ROLE_ADMIN authority.");
            throw new AccessDeniedException("Access is denied");
        }
        logger.info("Access granted. Proceeding with business logic for ROLE_ADMIN.");
        return "user/user_business";
    }
    
    @GetMapping("/dashboard/business/settings")    
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String createBusinessSettings(@RequestParam(value = "businessID", required = false) Long businessID, Model model) {
        logger.info("Attempting to access the /dashboard/business/settings endpoint.");
        User user = commonService.getLoggedInUser();
        
        if (businessID != null) {
            // Use the businessID parameter in your logic
            // For example, you can pass it to your service layer to fetch relevant data
            // Or you can add it to the model to make it available in your view
        	Business business = businessRepository.findById(businessID).get();
            model.addAttribute("businessID", businessID);
            model.addAttribute("config", business.getConfig());
        }
        
        if (user == null) {
            logger.warn("No logged-in user found.");
            throw new AccessDeniedException("Access is denied");
        }
        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
        logger.info("Logged-in user role: {}", userRole);
        if (!"ROLE_ADMIN".equals(userRole)) {
            logger.warn("Access denied. User does not have ROLE_ADMIN authority.");
            throw new AccessDeniedException("Access is denied");
        }
        logger.info("Access granted. Proceeding with business logic for ROLE_ADMIN.");
        return "user/user_business_settings";
    }
    
    @GetMapping("/dashboard/merchant")    
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String createMerchant() {
    	 logger.info("Attempting to access the /dashboard/merchant_user endpoint.");
         User user = commonService.getLoggedInUser();
         if (user == null) {
             logger.warn("No logged-in user found.");
             throw new AccessDeniedException("Access is denied");
         }
         String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
         logger.info("Logged-in user role: {}", userRole);
         if (!"ROLE_ADMIN".equals(userRole) && !"ROLE_MERCHANT_ADMIN".equals(userRole)) {
             logger.warn("Access denied. User does not have ROLE_ADMIN or ROLE_MERCHANT_ADMIN authority.");
             throw new AccessDeniedException("Access is denied");
         }
         logger.info("Access granted. Proceeding with merchant_user logic for ROLE_ADMIN or ROLE_MERCHANT_ADMIN.");
        return "user/user_merchant";
    }
    
    @GetMapping("/dashboard/region")    
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String createRegion() {
        logger.info("Attempting to access the /dashboard/business endpoint.");
        User user = commonService.getLoggedInUser();
        if (user == null) {
            logger.warn("No logged-in user found.");
            throw new AccessDeniedException("Access is denied");
        }
        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
        logger.info("Logged-in user role: {}", userRole);
        if (!"ROLE_ADMIN".equals(userRole)) {
            logger.warn("Access denied. User does not have ROLE_ADMIN authority.");
            throw new AccessDeniedException("Access is denied");
        }
        logger.info("Access granted. Proceeding with business logic for ROLE_ADMIN.");
        return "user/region";
    }
    
    @GetMapping("/dashboard/country")    
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String createCountry() {
        logger.info("Attempting to access the /dashboard/business endpoint.");
        User user = commonService.getLoggedInUser();
        if (user == null) {
            logger.warn("No logged-in user found.");
            throw new AccessDeniedException("Access is denied");
        }
        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
        logger.info("Logged-in user role: {}", userRole);
        if (!"ROLE_ADMIN".equals(userRole)) {
            logger.warn("Access denied. User does not have ROLE_ADMIN authority.");
            throw new AccessDeniedException("Access is denied");
        }
        logger.info("Access granted. Proceeding with business logic for ROLE_ADMIN.");
        return "user/country";
    }
    
    @GetMapping("/dashboard/merchant_user")    
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String createMerchantUser() {
        logger.info("Attempting to access the /dashboard/merchant_user endpoint.");
        User user = commonService.getLoggedInUser();
        if (user == null) {
            logger.warn("No logged-in user found.");
            throw new AccessDeniedException("Access is denied");
        }
        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
        logger.info("Logged-in user role: {}", userRole);
        if (!"ROLE_ADMIN".equals(userRole)) {
            logger.warn("Access denied. User does not have ROLE_ADMIN authority.");
            throw new AccessDeniedException("Access is denied");
        }
        logger.info("Access granted. Proceeding with merchant_user logic for ROLE_ADMIN.");
        return "user/merchant_user";
    }
    
    
    
    
    
    @RequestMapping(value = "/profile")
    public String getUserProfile(Principal principal, Model model, Authentication authentication) {
    	
    	
    	
    	 User user = commonService.getLoggedInUser();
    	    
    	 List<BillingSummary> getBillingSummaryByUser = userService.getBillingSummaryByUser(user);
    	 
    	   
    	    StringBuilder result = new StringBuilder();

    	    result.append("Billing Summaries:\n");
    	    for (BillingSummary billingSummary : getBillingSummaryByUser) {
    	        result.append("ID: ").append(billingSummary.getId()).append(", Total Amount: ").append(billingSummary.getTotalAmount()).append("\n");
    	    }

    	    result.append("\nRelated Merchant IDs:\n");
    	   
    	    System.out.println(" >>>>>>>>>>> : "  +result);
    	
        return"user/user_profile";
    }
    
    

    
    @RequestMapping(value="/dashboard/gallery", method = RequestMethod.GET)
    public String modelPhotos(Model model, Principal principal,
            @RequestParam(value = "businessID", required = false) Long businessID,
            @RequestParam(value = "merchantID", required = false) Long merchantID) {

        User user = commonService.getLoggedInUser();

        if (businessID == null && merchantID == null) {
            throw new RuntimeException("Missing Business/Merchant");
        }

        galleryService.authorize();

        Business business = null;
        List<Photo> photos = null;
        user = commonService.getLoggedInUser();
        Merchant merchant = null;
        Merchant selectedMerchant = null;



        if (merchantID != null) {
            merchant = merchantRepository.findById(merchantID).get();
            business = merchant.getBusiness();
           // photos = photoRepository.findByMerchantAndDeletedOrderBySequence(merchant, false);
            photos = photoRepository.findByBusinessIdAndMerchantId(businessID, merchantID);
            selectedMerchant = merchantRepository.findById(merchantID).orElse(null);
            for(Photo p : photos) {
            	System.out.println("m if : " + p.getId());
            }
            
        } else {
            if (user.getBusiness() != null)
                businessID = user.getBusiness().getId();
            business = businessRepository.findById(businessID).get();
            photos = photoRepository.findByBusinessID(businessID);
        }

        if (merchantID != null)
            model.addAttribute(AttributeConsts.ATTR_MERCHANT_LIST, merchantRepository.findById(merchantID).get());

       // model.addAttribute(AttributeConsts.ATTR_PHOTO_LIST, photos);
        model.addAttribute("photos_", photos);
        model.addAttribute(AttributeConsts.ATTR_BUSINESS, business);
        model.addAttribute("selectedMerchant", selectedMerchant);
        
        List<Merchant> merchants = merchantRepository.findMerchantByActive(businessID);
        model.addAttribute("merchants",merchants);
        

        return "user/gallery";
    }
    
    
    @RequestMapping(value="/dashboard/gallery_merchant", method = RequestMethod.GET)
    public String modelMerchantStaffPhotos(Model model, Principal principal,
            @RequestParam(value = "businessID", required = false) Long businessID,
            @RequestParam(value = "merchantID", required = false) Long merchantID,
            @RequestParam(value = "merchantStaffLink", required = false) Long merchantStaffLink) {

        User user = commonService.getLoggedInUser();

        if (merchantID == null) {
            throw new RuntimeException("Missing Merchant");
        }

        List<PhotoMerchant> photos = null;
        user = commonService.getLoggedInUser();
        Merchant merchant = null;
        User selectedMerchant = null;

//        if (merchantID != null) {
//            merchant = merchantRepository.findById(merchantID).get();
//            photos = photoRepository.findByBusinessIdAndMerchantId(businessID, merchantID);
//            
//            if(merchantStaffLink != null) {
//            	selectedMerchant = userRepository.findById(merchantStaffLink).orElse(null);
//            }
//          
//            
//        } 
        
        
        if (merchantStaffLink != null) {
            merchant = merchantRepository.findById(merchantID).get();
            photos = photoMerchantRepository.findByMerchantIdAndUserId(merchantID,merchantStaffLink);
            selectedMerchant = userRepository.findById(merchantStaffLink).orElse(null); 
        } else {
        	if (user.getMerchant() != null)
                merchantID = user.getMerchant().getId();
            merchant = merchantRepository.findById(merchantID).get();
            photos = photoMerchantRepository.findByMerchantId(merchantID);
        }

        if (merchantID != null)
            model.addAttribute(AttributeConsts.ATTR_MERCHANT_LIST, merchantRepository.findById(merchantID).get());

        model.addAttribute("photos_", photos);
        model.addAttribute("selectedMerchant", selectedMerchant);
        
        List<User> merchants = userRepository.findMerchantByActive(merchantID);
          
        model.addAttribute("merchants",merchants);
        model.addAttribute("merchant",merchant);
        

        return "user/my_gallery";
    }
    
    @GetMapping("/role")
    public ResponseEntity<String> getUserRole() {
        User user = commonService.getLoggedInUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
        }
        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
        return ResponseEntity.ok(userRole);
    }
    
    
    @GetMapping("/dashboard/billSummary")    
    public String BillSummary(Model model) {
    	 logger.info("Attempting to access the /dashboard/billSummary endpoint.");
         User user = commonService.getLoggedInUser();
         if (user == null) {
             logger.warn("No logged-in user found.");
             throw new AccessDeniedException("Access is denied");
         }
         String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
         logger.info("Logged-in user role: {}", userRole);
         if (!"ROLE_MERCHANT_STAFF".equals(userRole) && !"ROLE_MERCHANT_ADMIN".equals(userRole)) {
             logger.warn("Access denied. User does not have ROLE_MERCHANT_STAFF or ROLE_MERCHANT_ADMIN authority.");
             throw new AccessDeniedException("Access is denied");
         }
         
         model.addAttribute("cartItemStatuses", enumHelper.getCartItemStatuses());
         
         logger.info("Access granted. Proceeding with merchant_user logic for ROLE_ADMIN or ROLE_MERCHANT_ADMIN.");
        return "user/user_customer_billSummary";
    }
    
    @RequestMapping(value = "/dashboard/billing/show_invoice", method=RequestMethod.GET)
	public void showInvoice(Model model,
			@RequestParam(value = "invoice_id") Long invoiceID,HttpServletResponse response) {
    	
    	User user = commonService.getLoggedInUser();
		
		if(user.getRole().getName() != RoleType.ROLE_MERCHANT_ADMIN 
				&& user.getRole().getName() != RoleType.ROLE_ADMIN)
			throw new RuntimeException("Not Allowed");
		
		CartItem invoice = cartItemRepository.findById(invoiceID).get();
		
		byte[] buf = cartService.exportInvoice(invoice);
		
		String fileName = cartService.getInvoiceTitle(invoice);
		
		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		
		try {
			response.getOutputStream().write(buf);
		}catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
//    @RequestMapping(value = "/dashboard/export")
//	public void exportActivity(HttpServletResponse response,
//			Model model,Pageable pageable,
//			@RequestParam(value = "name",required = false) String name,
//			@RequestParam(value = "merchantID",required = false) Long  merchantID,
//	         @RequestParam(value = "startDate", required = false) String startDate,
//	         @RequestParam(value = "endDate", required = false) String endDate,
//	         @RequestParam(value = "email", required = false) String email,
//	         @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
//	         @RequestParam(value = "status", required = false) CartItemStatus status) throws IOException{
//		
//		User user = commonService.getLoggedInUser();
//		
//		Business business = user.getBusiness();
//		
//		response.setContentType(ACTIVITY_EXPORT_CONTENT_TYPE);
//		String fileName = String.format(ACTIVITY_EXPORT_FILE_NAME, "dynamicValue");
//		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName +"\"");
//		cartService.exportBillSummary(response.getOutputStream(),pageable, user,merchantID, name,email,mobileNumber,status,startDate,endDate);
//	}
    
//    @RequestMapping(value = "/dashboard/export",method = RequestMethod.GET)
//	@ResponseBody
//    public void exportActivity(HttpServletResponse response,
//                               Pageable pageable,
//                               @RequestParam(value = "name", required = false) String name,
//                               @RequestParam(value = "merchantID", required = false) Long merchantID,
//                               @RequestParam(value = "startDate", required = false) String startDate,
//                               @RequestParam(value = "endDate", required = false) String endDate,
//                               @RequestParam(value = "email", required = false) String email,
//                               @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
//                               @RequestParam(value = "status", required = false) CartItemStatus status) throws IOException {
//        try {
//            User user = commonService.getLoggedInUser();
//            Business business = user.getBusiness();
//
//            response.setContentType(ACTIVITY_EXPORT_CONTENT_TYPE);
//            String fileName = String.format(ACTIVITY_EXPORT_FILE_NAME, "dynamicValue");
//            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//            cartService.exportBillSummary(response.getOutputStream(), pageable, user, merchantID, name, email, mobileNumber, status, startDate, endDate);
//        } catch (Exception e) {
//            logger.error("Error exporting activity: {}", e.getMessage(), e);
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error exporting activity");
//        }
//    }
    
    
    
    
    @RequestMapping(value = "/dashboard/export", method = RequestMethod.GET)
    @ResponseBody
    public void exportActivity(HttpServletResponse response,
                               Pageable pageable,
                               @RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "merchantID", required = false) Long merchantID,
                               @RequestParam(value = "startDate", required = false) String startDate,
                               @RequestParam(value = "endDate", required = false) String endDate,
                               @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
                               @RequestParam(value = "status", required = false) CartItemStatus status,
                               @RequestParam(value = "usePagination", required = false, defaultValue = "true") boolean usePagination) throws IOException {
        try {
            User user = commonService.getLoggedInUser();
            Business business = user.getBusiness();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = String.format("activity_export_%s.xlsx", "dynamicValue");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            
            cartService.exportBillSummary(response.getOutputStream(), pageable, user, merchantID, name, email, mobileNumber, status, startDate, endDate, usePagination);
        } catch (Exception e) {
            logger.error("Error exporting activity: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error exporting activity");
        }
    }
    
    @GetMapping("/dashboard/customer")    
    public String CustomerSummary(Model model) {
    	 logger.info("Attempting to access the /dashboard/billSummary endpoint.");
         User user = commonService.getLoggedInUser();
         if (user == null) {
             logger.warn("No logged-in user found.");
             throw new AccessDeniedException("Access is denied");
         }
         String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
         logger.info("Logged-in user role: {}", userRole);
         if (!"ROLE_MERCHANT_STAFF".equals(userRole) && !"ROLE_MERCHANT_ADMIN".equals(userRole)) {
             logger.warn("Access denied. User does not have ROLE_MERCHANT_STAFF or ROLE_MERCHANT_ADMIN authority.");
             throw new AccessDeniedException("Access is denied");
         }
         
         model.addAttribute("cartItemStatuses", enumHelper.getCartItemStatuses());
         
         logger.info("Access granted. Proceeding with merchant_user logic for ROLE_ADMIN or ROLE_MERCHANT_ADMIN.");
        return "user/customer_pagewise";
    }


}
