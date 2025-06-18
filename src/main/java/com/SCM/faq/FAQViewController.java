package com.SCM.faq;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.SCM.entities.User;
import com.SCM.service.CommonService;

@Controller
@RequestMapping(value="/user")
public class FAQViewController {
	
	@Autowired
	CommonService commonService;
	@Autowired
	FAQListRepository faqListRepository;
	
	 private static final Logger logger = LoggerFactory.getLogger(FAQViewController.class);
	
	@GetMapping("/dashboard/faq")    
    public String createFAQ(Model model) {
        logger.info("Attempting to access the /dashboard/faq endpoint.");
        User user = commonService.getLoggedInUser();
        if (user == null) {
            logger.warn("No logged-in user found.");
            throw new AccessDeniedException("Access is denied");
        }
        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
        logger.info("Logged-in user role: {}", userRole);
        if (!"ROLE_MERCHANT_ADMIN".equals(userRole)) {
            logger.warn("Access denied. User does not have ROLE_MERCHANT_ADMIN authority.");
            throw new AccessDeniedException("Access is denied");
        }
        
        List<FAQList> faqLists = faqListRepository.findByBusiness(user.getBusiness());
        model.addAttribute("faqLists",faqLists);
        
        logger.info("Access granted. Proceeding with business logic for ROLE_MERCHANT_ADMIN");
        return "user/user_faq";
    }
	
	@GetMapping("/dashboard/faqlist")    
    public String createFAQList() {
        logger.info("Attempting to access the /dashboard/faqlist endpoint.");
        User user = commonService.getLoggedInUser();
        if (user == null) {
            logger.warn("No logged-in user found.");
            throw new AccessDeniedException("Access is denied");
        }
        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
        logger.info("Logged-in user role: {}", userRole);
        if (!"ROLE_MERCHANT_ADMIN".equals(userRole)) {
            logger.warn("Access denied. User does not have ROLE_MERCHANT_ADMIN authority.");
            throw new AccessDeniedException("Access is denied");
        }
        logger.info("Access granted. Proceeding with business logic for ROLE_MERCHANT_ADMIN");
        return "user/user_faqlist";
    }

}
