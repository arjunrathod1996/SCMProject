package com.SCM.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.Summary;
import com.SCM.entities.User;
import com.SCM.leads.LeadMainRepository;
import com.SCM.repository.BusinessRepository;
import com.SCM.utils.Utils2;

@Service
public class DashboardService {
	
		
		@Autowired
		CommonService commonService;
		@Autowired
		BusinessRepository businessRepository;
		@Autowired
		MessageSource messageSource;
		@Autowired
		LeadMainRepository leadMainRepository;
	
	
		public List<Summary> initAllowedFeature(Merchant mainOutlet) {
		System.out.println("initAllowedFeature is being called!");  // Add this line
		List<Long>merchantIDs = null;
		Business business = null;
		if(mainOutlet == null)
			return null;
		merchantIDs = commonService.getMerchantIDs(mainOutlet.getBusiness());
		business = businessRepository.findById(mainOutlet.getId()).get();
		
		List<Summary> allowedFeatures = new ArrayList<>();
		// Reward Code submission
		allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.faq"),
				Utils2.getMessage(messageSource, "me.faq"),
				"btn btn-default",
				"fas fa-question-circle fa-2x", null,"/my_app/access/faq/" + mainOutlet.getShortLink()));
		//Bill Submission
		if(business.getConfig().getEnableBillSumbission()) {
			allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.submit_bill"),
					Utils2.getMessage(messageSource, "me.submit_bill"),
					"btn btn-default",
					"fa fa-upload fa-2x", null,"/my/bill/" + mainOutlet.getShortLink()));
		}
		//Reward Prfile
		allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.profile"),
				Utils2.getMessage(messageSource, "me.profile"),
				"btn btn-default",
				"fa fa-user fa-2x", null,"/my_app/access/user/profile/" + mainOutlet.getShortLink()));
		if(business.getConfig().getEnableAppointment()) {
			allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.appointment"),
					Utils2.getMessage(messageSource, "me.appointment"),
					"btn btn-default",
					"fa fa-calendar fa-2x", null,"/my/feedback/" + mainOutlet.getShortLink()));
		}
		return allowedFeatures;
	}
		
//		public int getLeadCountByUserRole(User user) {
//	        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
//
//	        if ("ROLE_ADMIN".equals(userRole)) {
//	            // If user is ROLE_ADMIN, count all lead mains
//	            return (int) leadMainRepository.count();
//	        } else if ("ROLE_MERCHANT_ADMIN".equals(userRole)) {
//	            // If user is ROLE_MERCHANT_ADMIN, count lead mains for the merchant
//	            return leadMainRepository.countByCreatedBy(user);
//	        } else if ("ROLE_MERCHANT_STAFF".equals(userRole)) {
//	            // If user is ROLE_MERCHANT_STAFF, count lead mains created by the user
//	            return leadMainRepository.countByCreatedBy(user);
//	        } else {
//	            throw new IllegalArgumentException("Unsupported role: " + userRole);
//	        }
//	    }
		
		
		public int getLeadCountByUserRole(User user) {
	        int leadCount = 0;
	        String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
	        switch (userRole) {
	            case "ROLE_ADMIN":
	                leadCount = (int) leadMainRepository.count();
	                break;
	            case "ROLE_MERCHANT_ADMIN":
	                leadCount = leadMainRepository.countByCreatedBy(user);
	                break;
	            case "ROLE_MERCHANT_STAFF":
	                leadCount = leadMainRepository.countByCreatedBy(user);
	                break;
	            default:
	                throw new IllegalArgumentException("Unsupported role: " + userRole);
	        }
	        return leadCount;
	    }
		
		public Map<String, Integer> getLeadCountsByUserRole(User user) {
		    Map<String, Integer> leadCounts = new HashMap<>();
		    String userRole = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN";
		    switch (userRole) {
		        case "ROLE_ADMIN":
		            leadCounts.put("totalLeads", (int) leadMainRepository.count());
		            // Add more counts if needed
		            break;
		        case "ROLE_MERCHANT_ADMIN":
		            leadCounts.put("leadsCreatedByMerchantAdmin", leadMainRepository.countByCreatedBy(user));
		            // Add more counts if needed
		            break;
		        case "ROLE_MERCHANT_STAFF":
		            leadCounts.put("leadsCreatedByMerchantStaff", leadMainRepository.countByCreatedBy(user));
		            // Add more counts if needed
		            break;
		        default:
		            throw new IllegalArgumentException("Unsupported role: " + userRole);
		    }
		    return leadCounts;
		    
		}

}
