package com.SCM.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.SCM.businessConfig.Config;
import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.Customer;
import com.SCM.entities.Summary;
import com.SCM.repository.BusinessRepository;
import com.SCM.utils.Utils2;

@Service
public class MyViewService {
	
	@Autowired 
	CommonService commonService;
	@Autowired
	MessageSource messageSource;
	@Autowired
	BusinessRepository businessRepository;
	
//	public List<Summary> initAllowedFeature(Merchant mainOutlet) {
//		System.out.println("initAllowedFeature is being called!");  // Add this line
//		List<Long>merchantIDs = null;
//		Business business = null;
//		if(mainOutlet == null)
//			return null;
//		merchantIDs = commonService.getMerchantIDs(mainOutlet.getBusiness());
//		business = businessRepository.findById(mainOutlet.getId()).get();
//		
//		List<Summary> allowedFeatures = new ArrayList<>();
//		// Reward Code submission
//		allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.faq"),
//				Utils2.getMessage(messageSource, "me.faq"),
//				"btn btn-default",
//				"fas fa-question-circle fa-2x", null,"/my_app/access/faq/" + mainOutlet.getShortLink()));
//		//Bill Submission
//		if(business.getConfig().getEnableBillSumbission()) {
//			allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.submit_bill"),
//					Utils2.getMessage(messageSource, "me.submit_bill"),
//					"btn btn-default",
//					"fa fa-upload fa-2x", null,"/my/bill/" + mainOutlet.getShortLink()));
//		}
//		//Reward Prfile
//		allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.profile"),
//				Utils2.getMessage(messageSource, "me.profile"),
//				"btn btn-default",
//				"fa fa-user fa-2x", null,"/my_app/access/user/profile/" + mainOutlet.getShortLink()));
//		if(business.getConfig().getEnableAppointment()) {
//			allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.appointment"),
//					Utils2.getMessage(messageSource, "me.appointment"),
//					"btn btn-default",
//					"fa fa-calendar fa-2x", null,"/my/feedback/" + mainOutlet.getShortLink()));
//		}
//		return allowedFeatures;
//	}
	
	
	
	public List<Summary> initAllowedFeature(Merchant mainOutlet) {
	    System.out.println("initAllowedFeature is being called!");  // Add this line
	    List<Long> merchantIDs = null;
	    Business business = null;
	    if (mainOutlet == null)
	        return null;
	    merchantIDs = commonService.getMerchantIDs(mainOutlet.getBusiness());
	    business = businessRepository.findById(mainOutlet.getId()).orElse(null);

	    if (business == null || business.getConfig() == null) {
	        System.out.println("Business or its config is null");
	        return null;
	    }

	    Config config = business.getConfig();
	    List<Summary> allowedFeatures = new ArrayList<>();
	    
	    // Reward Code submission
	    allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.faq"),
	            Utils2.getMessage(messageSource, "me.faq"),
	            "btn btn-default",
	            "fas fa-question-circle fa-2x", null, "/my_app/access/faq/" + mainOutlet.getShortLink()));

	    // Bill Submission
	    Boolean enableBillSubmission = config.getEnableBillSumbission();
	    if (Boolean.TRUE.equals(enableBillSubmission)) {
	        allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.submit_bill"),
	                Utils2.getMessage(messageSource, "me.submit_bill"),
	                "btn btn-default",
	                "fa fa-upload fa-2x", null, "/my/bill/" + mainOutlet.getShortLink()));
	    }

	    // Reward Profile
	    allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.profile"),
	            Utils2.getMessage(messageSource, "me.profile"),
	            "btn btn-default",
	            "fa fa-user fa-2x", null, "/my_app/access/user/profile/" + mainOutlet.getShortLink()));

	    // Appointment
	    Boolean enableAppointment = config.getEnableAppointment();
	    if (Boolean.TRUE.equals(enableAppointment)) {
	        allowedFeatures.add(new Summary(Utils2.getMessage(messageSource, "me.appointment"),
	                Utils2.getMessage(messageSource, "me.appointment"),
	                "btn btn-default",
	                "fa fa-calendar fa-2x", null, "/my/feedback/" + mainOutlet.getShortLink()));
	    }
	    
	    return allowedFeatures;
	}

	
	public Object getProfileScore(Customer customer) {
			Integer score = 40;
			if(customer.getGender() != null) {
				score += 20;
			}
			if(customer.getBithday() != null) {
				score += 20;
			}
			return score;
		}

}
