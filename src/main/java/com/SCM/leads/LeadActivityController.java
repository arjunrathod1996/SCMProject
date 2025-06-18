package com.SCM.leads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.SCM.entities.User;
import com.SCM.leads.LeadActivity.ActivityType;
import com.SCM.service.CommonService;

@RequestMapping("/api/leads/activities")
@Controller
public class LeadActivityController {

	@Autowired
	LeadActivityService leadActivityService;
	@Autowired
	CommonService commonService;
	@Autowired
	LeadContactRepository leadContactRepositoty;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@GetMapping("/task")
    public ResponseEntity<?> getContact(
            @RequestParam(value = "id") Long activityID) {
	
        // Retrieve the logged-in user using the authentication object
        User loggedInUser = commonService.getLoggedInUser();
       
        return new ResponseEntity<LeadActivity>(leadActivityService.getTaskActivity(activityID, loggedInUser), HttpStatus.OK);
    }
	
	@PostMapping("/task")
    public ResponseEntity<?> createOrUpdatePot(
    		@RequestBody LeadActivity leadActivity,
            @RequestParam(value = "leadID", required = false) Long leadID,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "contactID", required = false) Long contactID) {
		
		System.out.println(" leadID : " + leadID);
		System.out.println(" id : " + id);
		System.out.println(" contactID : " + contactID);
	
        // Retrieve the logged-in user using the authentication object
        User loggedInUser = commonService.getLoggedInUser();
       
        if (loggedInUser == null) {
            // Handle the case when no user is logged in
            return new ResponseEntity<>("No user is logged in.", HttpStatus.UNAUTHORIZED);
        }

        try {
        	leadActivity.setId(id);
        	leadActivity.setContact(leadContactRepositoty.findById(contactID).get());
            leadActivity = leadActivityService.addOrUpdateTaskTrace(loggedInUser,leadID, leadActivity);
        } catch (Exception ex) {
        	logger.debug("Failed to create LeadContacts : {} ", ex.getMessage());
            ex.printStackTrace();
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<LeadActivity>(leadActivity, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/page_list", method = RequestMethod.GET)
	@ResponseBody
	public Page<?> getLeadContacts(Pageable pageable,
			@RequestParam(value = "activityType",required = false) ActivityType activityType,
			@RequestParam(value = "message",required = false) String  message,
			@RequestParam(value = "leadName",required = false) String  leadName,
			@RequestParam(value = "leadID",required = false) String  leadID,
			@RequestParam(value = "assignee",required = false) String  assignee,
			@RequestParam(value = "creator",required = false) String  creator,
			@RequestParam(value = "contactID",required = false) Long  ContatctID,
			@RequestParam(value = "startDate",required = false) String startDate,
			@RequestParam(value = "endDate",required = false) String endDate) {
	 
		User user = commonService.getLoggedInUser();
		
	    return leadActivityService.getLeadActivities(user, pageable, activityType, message, leadName, leadID, assignee, creator, ContatctID, startDate, endDate);
	}
	
}
