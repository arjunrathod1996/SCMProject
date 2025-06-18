package com.SCM.leads;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.kafka.KafkaUtils;
import com.SCM.kafka.LeadKafkaProducer;
import com.SCM.leads.LeadMain.Priority;
import com.SCM.leads.LeadMain.Status;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role.RoleType;
import com.SCM.service.CommonService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/leads")
public class LeadMainController {
	
	@Autowired
	LeadMainRepository leadMainRepository;
	@Autowired
	LeadActivityRepository leadActivityRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	LeadMainService leadMainService;
	@Autowired
	CommonService commonService;
	@Autowired
	LeadMainReportService leadMainReportService;
	@Autowired
	LeadKafkaProducer leadKafkaProducer;
	@Value("${kafka.enabled}")
	private boolean kafkaEnabled;


	
	// private Logger logger = LoggerFactory.getLogger(this.getClass());
	 
	 private static final Logger logger = LoggerFactory.getLogger(LeadMainController.class);
	    private static final String KAFKA_HOST = "localhost";
	    private static final int KAFKA_PORT = 9092;
	
	
	 @RequestMapping(value = "", method = RequestMethod.GET)
	    public ResponseEntity<LeadMain> getLead(@RequestParam("id") Long leadMainId) {
	        try {
	            User user = commonService.getLoggedInUser();
	            Business business = leadMainService.getBusiness(user);
	            LeadMain lead = leadMainService.getLead(leadMainId);
	            return new ResponseEntity<>(lead, HttpStatus.OK);
	        } catch (Exception e) {
	            logger.error("Error fetching lead", e);
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

	    @RequestMapping(value = "/search", method = RequestMethod.GET)
	    public List<LeadMain> searchLeadMain(@RequestParam(value = "name") String name) {
	        try {
	            User user = commonService.getLoggedInUser();
	            logger.debug("Logged in user ID: {}, email: {}", user.getId(), user.getEmail());
	            if (RoleType.ROLE_MERCHANT_ADMIN.equals(user.getRole().getName())) {
	                return leadMainRepository.findByBusinessAndDeletedAndNameContaining(user.getBusiness(), false, name);
	            } else {
	                return leadMainRepository.findByBusinessAndDeletedAndAssignedToAndNameContaining(user.getBusiness(), false, user, name);
	            }
	        } catch (Exception e) {
	            logger.error("Error searching lead", e);
	            return Collections.emptyList();
	        }
	    }
	
//	@RequestMapping(value="",method = RequestMethod.POST)
//	public ResponseEntity<?> createOrUpdate(@RequestBody LeadMain leadMain,
//			@RequestParam(value = "id", required = false) Long leadId,
//			@RequestParam(value = "merchantID", required = false) Long merchantID){
//		
//		User user = commonService.getLoggedInUser();
//		
//		if(leadId != null)
//			leadMain.setId(leadId);
//		
//		if(merchantID != null)
//			leadMain.setMerchant(merchantRepository.findById(merchantID).get());
//		
//		leadMain = leadMainService.createOrUpdate(user, leadMain);
//		
//		return new ResponseEntity<LeadMain>(leadMain,HttpStatus.OK);
//	}
	    
//	    @RequestMapping(value = "", method = RequestMethod.POST)
//	    public ResponseEntity<?> createOrUpdate(@RequestBody LeadMain leadMain,
//	                                            @RequestParam(value = "id", required = false) Long leadId,
//	                                            @RequestParam(value = "merchantID", required = false) Long merchantID) {
//	        User user = commonService.getLoggedInUser();
//
//	        if (leadId != null) {
//	            leadMain.setId(leadId);
//	        }
//
//	        if (merchantID != null) {
//	            leadMain.setMerchant(merchantRepository.findById(merchantID).get());
//	        }
//
//	        leadMain = leadMainService.createOrUpdate(user, leadMain);
//
//	        // Convert leadMain to JSON and send to Kafka
//	        try {
//	            ObjectMapper objectMapper = new ObjectMapper();
//	            String leadJson = objectMapper.writeValueAsString(leadMain);
//	            leadKafkaProducer.sendLeadMessage(leadJson);
//	        } catch (Exception e) {
//	            logger.error("Error sending lead to Kafka", e);
//	        }
//
//	        return new ResponseEntity<>(leadMain, HttpStatus.OK);
//	    }
//   
	 
	    @RequestMapping(value = "", method = RequestMethod.POST)
	    public ResponseEntity<?> createOrUpdate(@RequestBody LeadMain leadMain,
	                                            @RequestParam(value = "id", required = false) Long leadId,
	                                            @RequestParam(value = "merchantID", required = false) Long merchantID) {
	        User user = commonService.getLoggedInUser();

	        if (leadId != null) {
	            leadMain.setId(leadId);
	        }

	        if (merchantID != null) {
	            leadMain.setMerchant(merchantRepository.findById(merchantID).orElse(null));
	        }

	        leadMain = leadMainService.createOrUpdate(user, leadMain);

	        // Convert leadMain to JSON
	        String leadJson;
	        try {
	            ObjectMapper objectMapper = new ObjectMapper();
	            leadJson = objectMapper.writeValueAsString(leadMain);
	        } catch (Exception e) {
	            logger.error("Error converting leadMain to JSON", e);
	            return new ResponseEntity<>("Error processing lead data", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        
	        

	        // Check Kafka availability and handle accordingly
	        if (kafkaEnabled) {
	            if (KafkaUtils.isKafkaRunning(KAFKA_HOST, KAFKA_PORT)) {
	                try {
	                    logger.info("Kafka is running. Sending lead to Kafka topic: leads_topic");
	                    leadKafkaProducer.sendLeadMessage(leadJson);
	                } catch (Exception e) {
	                    logger.error("Error sending lead to Kafka", e);
	                    // Optionally, handle errors in Kafka sending logic
	                    return new ResponseEntity<>("Error sending lead to Kafka", HttpStatus.INTERNAL_SERVER_ERROR);
	                }
	            } else {
	                logger.warn("Kafka is not running. Executing alternative flow.");
	                // Execute the normal flow or alternative actions
	                processNormalFlow(leadJson);
	            }
	        } else {
	            logger.info("Kafka is disabled. Executing normal flow.");
	            processNormalFlow(leadJson);
	        }


	        return new ResponseEntity<>(leadMain, HttpStatus.OK);
	    }

	    private void processNormalFlow(String leadJson) {
	        logger.info("Executing normal flow for lead: {}", leadJson);
	        // Implementation of alternative actions when Kafka is not available
	        // For example, save the message to a database or queue it for later
	        // ...
	    }
	
	@RequestMapping(value = "/assign",method = RequestMethod.GET)
	public ResponseEntity<?> assign(@RequestParam("id") Long leadMainId,
			@RequestParam(value = "assigneeId" , required = false) Long assigneeID,
			@RequestParam(value = "message",required = false) String message){
		
		User user = commonService.getLoggedInUser();
		
		LeadMain lead = leadMainRepository.findById(leadMainId).get();
		
		User assignee = null;
		
		if(assigneeID != null)
			assignee = userRepository.findById(assigneeID).get();
		
		lead = leadMainService.assignLead(user, lead, assignee, message);
		
		return new ResponseEntity<LeadMain>(lead,HttpStatus.OK);
	}	
	
	@RequestMapping(value = "/add_activity",method = RequestMethod.POST)
	public ResponseEntity<?> addActivity(@RequestBody LeadActivity leadActivity,
			@RequestParam(value = "leadID") Long leadID){
		User user = commonService.getLoggedInUser();
		if(user.getRole().getName() != RoleType.ROLE_MERCHANT_ADMIN 
				&& user.getRole().getName() != RoleType.ROLE_MERCHANT_STAFF) {
			throw new RuntimeException("Not Allowed");
		}
		LeadActivity activity = leadMainService.addActivity(user, leadID, leadActivity);
		return new ResponseEntity<LeadActivity>(activity,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/add_contact", method=RequestMethod.POST)
	public ResponseEntity<?> addContact(@RequestBody LeadContactOld leadContactOld,
			@RequestParam("id") Long id,
			@RequestParam(value = "contactId",required = false) String contactID){
		
		User user = commonService.getLoggedInUser();
		
		LeadMain lead = leadMainRepository.findById(id).get();
		
		if(contactID != null && !contactID.isEmpty())
			leadContactOld.setContactID(contactID);
		
		lead = leadMainService.addContact(user, lead, leadContactOld);
		
		lead = leadMainRepository.save(lead);
				
				
		return new ResponseEntity<LeadMain>(lead,HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/delete_contact", method=RequestMethod.GET)
	public ResponseEntity<?> deleteContact(@RequestParam("leadID") Long id,
			@RequestParam("contactId") String contactID){
		
		User user = commonService.getLoggedInUser();
		
		LeadMain lead = leadMainService.deleteContact(user, id, contactID);
		
		return new ResponseEntity<LeadMain>(lead,HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/primary", method=RequestMethod.GET)
	public ResponseEntity<?> markPrimary(@RequestParam("id") Long leadMainId,
			@RequestParam("contactId") String contactID){
		
		User user = commonService.getLoggedInUser();
		
		LeadMain lead = leadMainService.markPrimaryContact(user, leadMainRepository.findById(leadMainId).get(), contactID);
		
		return new ResponseEntity<LeadMain>(lead,HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/markImportant", method=RequestMethod.GET)
	public ResponseEntity<?> setStatus(@RequestParam("id") Long leadMainId){
		
		User user = commonService.getLoggedInUser();
		
		LeadMain leadMain = leadMainService.setImportantStatus(leadMainId);
		
		return new ResponseEntity<LeadMain>(leadMain,HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/page_list", method = RequestMethod.GET)
	@ResponseBody
	public Page<?> getLeads(Pageable pageable,@RequestParam(value = "leadName",required = false) String leadName,
			@RequestParam(value = "leadID",required = false) String  leadID,
			@RequestParam(value = "assigneeID",required = false) Long  assigneeID,
			@RequestParam(value = "important",required = false) Boolean  important,
			@RequestParam(value = "priority",required = false) Priority  priority,
			@RequestParam(value = "status",required = false) Status  status,
			@RequestParam(value = "followUpStartDate",required = false) String  followUpStartDate,
			@RequestParam(value = "followUpEndDate",required = false) String  followUpEndDate,
			@RequestParam(value = "startDate",required = false) String  startDate,
			@RequestParam(value = "endDate",required = false) String  endDate) {
		
		User user = commonService.getLoggedInUser();
		
		logger.debug("Logged in user ID: {}, email: {}", user.getId(), user.getEmail());
		
	    return leadMainReportService.getLeadList(user, pageable, leadName, leadID, assigneeID, important, priority, status, followUpStartDate, followUpEndDate, startDate, endDate);

	}
	
}

