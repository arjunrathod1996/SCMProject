package com.SCM.leads;

import java.util.List;

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
import com.SCM.role.Role.RoleType;
import com.SCM.service.CommonService;

@RequestMapping("/api/leads/contacts")
@Controller
public class LeadContactController {
	
	@Autowired
	LeadContactRepository leadContactRepositoty;
	@Autowired
	LeadMainRepository leadMainRepository;
	@Autowired
	LeadContactService leadContactService;
	@Autowired
	CommonService commonService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@GetMapping("")
	public ResponseEntity<?> getContact(
	        @RequestParam(value = "id") Long contactID) {

	    // Retrieve the logged-in user using the authentication object
	    User loggedInUser = commonService.getLoggedInUser();

	    // Retrieve the LeadContact object based on the contactID
	    LeadContact leadContact = leadContactService.getContact(contactID, loggedInUser);

	    if (leadContact == null) {
	        // Handle the case when the contact is not found
	        return new ResponseEntity<>("Contact not found", HttpStatus.NOT_FOUND);
	    }

	    return new ResponseEntity<LeadContact>(leadContact, HttpStatus.OK);
	}

	
	@GetMapping("/search")
    public ResponseEntity<?> searchContact(
            @RequestParam(value = "leadId") Long leadID,
            @RequestParam(value = "name") String name) {
	
        // Retrieve the logged-in user using the authentication object
        User loggedInUser = commonService.getLoggedInUser();
        
       LeadMain leadMain = leadMainRepository.findById(leadID).get();
       List<LeadContact> leadContacts = leadContactRepositoty.findByLeadMainAndFirstNameContaining(leadMain, name);
        return new ResponseEntity<List<LeadContact>>(leadContacts, HttpStatus.OK);
    }
	
	@PostMapping("")
    public ResponseEntity<?> createOrUpdateLeads(
    		@RequestBody LeadContact leadContact,
            @RequestParam(value = "leadID", required = false) Long leadID,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "mainId", required = false) Long mainId) {
	
        // Retrieve the logged-in user using the authentication object
        User loggedInUser = commonService.getLoggedInUser();
       
        if (loggedInUser == null) {
            // Handle the case when no user is logged in
            return new ResponseEntity<>("No user is logged in.", HttpStatus.UNAUTHORIZED);
        }
        
        if(loggedInUser.getRole().getName() != RoleType.ROLE_MERCHANT_ADMIN)
        	return new ResponseEntity<String>("Not Allowed",HttpStatus.BAD_REQUEST);

        try {
        	leadContact.setId(id);
            leadContact = leadContactService.createOrUpdate(loggedInUser,leadID,leadContact);
        } catch (Exception ex) {
        	logger.debug("Failed to create LeadContacts : {} ", ex.getMessage());
            ex.printStackTrace();
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<LeadContact>(leadContact, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/page_list", method = RequestMethod.GET)
	@ResponseBody
	public Page<?> getLeadContacts(Pageable pageable,
			@RequestParam(value = "name",required = false) String name,
			@RequestParam(value = "email",required = false) String  email,
			@RequestParam(value = "phoneNumber",required = false) String  phoneNumber,
			@RequestParam(value = "designation",required = false) String  designation,
			@RequestParam(value = "leadName",required = false) String  leadName,
			@RequestParam(value = "leadID",required = false) String  leadID,
			@RequestParam(value = "creator",required = false) String  creator,
			@RequestParam(value = "important",required = false) Boolean  important,
			@RequestParam(value = "startDate",required = false) String startDate,
			@RequestParam(value = "endDate",required = false) String endDate) {
	 
		User user = commonService.getLoggedInUser();
		
	    return leadContactService.getLeadContacts(user, pageable, name, email, phoneNumber, designation, leadName, leadID, creator, important, startDate, endDate);

	}

}
