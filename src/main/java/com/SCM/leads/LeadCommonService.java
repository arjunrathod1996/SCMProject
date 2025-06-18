package com.SCM.leads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.service.CommonService;

@Service
public class LeadCommonService {
	
	@Autowired
	CommonService commonService;
	
	public void checkEligibility(User loggedInUser, LeadMain leadMain){
		
		Business business = loggedInUser.getBusiness();
		
		if(business != null) {
			if(!leadMain.getBusiness().equals(business))
				throw new RuntimeException("Not Allowed");
		}
		
		if(loggedInUser.getMerchant() != null) {
			if(!leadMain.getAssignedTo().equals(loggedInUser))
				throw new RuntimeException("Not Allowed , Lead is not assigned to " + loggedInUser.getEmail());
		}
		
	}

}

