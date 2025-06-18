package com.SCM.leads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.kafka.LeadKafkaProducer;
import com.SCM.leads.LeadActivity.ActivityType;
import com.SCM.repository.BusinessRepository;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role;
import com.SCM.role.Role.RoleType;
import com.SCM.role.RoleRepository;
import com.SCM.service.CommonService;
import com.SCM.utils.CodeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LeadMainService {
	
	public static String[] DEFAULT_LEAD_SOURCE = {"Store Visit","Enquiry","Campaign"};
	
	// Limit for select top source of leads
	public static Integer TOP_SOURCE_LIMIT = 6;
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	LeadMainRepository leadMainRepository;
	@Autowired
	LeadActivityRepository leadActivityRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	CommonService commonService;
	@Autowired
	LeadActivityService leadActivityService;
	@Autowired
	RoleRepository roleRepository;
	@Autowired(required = false)
    private LeadKafkaProducer leadKafkaProducer;
	
	
private Logger logger = LoggerFactory.getLogger(getClass());
	
//	public LeadMain createOrUpdate(User user , LeadMain leadMain) {
//		
//		System.out.println("lead Name : " + leadMain.getName());
//
//		Business business = getBusiness(user,leadMain);
//
//		if (leadMain.getId() != null) {
//			
//			System.out.println("------>>>> updated");
//			
//			// update
//			leadMain = updateLead(user,leadMain);
//		} else {
//			// create
//			System.out.println("------>>>> create");
//			if (leadMain.getName() == null || leadMain.getName().isEmpty())
//				throw new RuntimeException("Missing Details");
//			
//			String source = null;
//
//			if (leadMain.getSource() != null && leadMain.getSource().isEmpty())
//				source = leadMain.getSource();
//			
//			leadMain.setBusiness(business);
//			leadMain.setCreatedBy(user);
//			String leadId = null;
//			Merchant merchant = null; // Initialize with the appropriate merchant
//			merchant = user.getMerchant();
//		
//			if(user.getBusiness() != null)
//				merchant = commonService.getMainOutlet(business);
//						
//			leadId = CodeUtils.getRecordID(merchant.getId());
//			leadMain.setLeadID(leadId);
//			leadMain.setStatus(LeadMain.Status.Open);
//			leadMain.setSource(source);
//			leadMain = leadMainRepository.save(leadMain);
//			
//			if(user.getRole().getName() == RoleType.ROLE_MERCHANT_STAFF)
//				assignLead(user,leadMain,user,"");
//		}
//		return leadMain;
//	}


public LeadMain createOrUpdate(User user, LeadMain leadMain) {
    System.out.println("lead Name : " + leadMain.getName());

    Business business = getBusiness(user, leadMain);

    if (leadMain.getId() != null) {
        System.out.println("------>>>> updated");
        // update
        leadMain = updateLead(user, leadMain);
    } else {
        // create
        System.out.println("------>>>> create");
        if (leadMain.getName() == null || leadMain.getName().isEmpty())
            throw new RuntimeException("Missing Details");

        String source = null;

        if (leadMain.getSource() != null && leadMain.getSource().isEmpty())
            source = leadMain.getSource();

        leadMain.setBusiness(business);
        leadMain.setCreatedBy(user);
        String leadId = null;
        Merchant merchant = null; // Initialize with the appropriate merchant
        merchant = user.getMerchant();

        if (user.getBusiness() != null)
            merchant = commonService.getMainOutlet(business);

        leadId = CodeUtils.getRecordID(merchant.getId());
        leadMain.setLeadID(leadId);
        leadMain.setStatus(LeadMain.Status.Open);
        leadMain.setSource(source);
        leadMain = leadMainRepository.save(leadMain);

        if (user.getRole().getName() == RoleType.ROLE_MERCHANT_STAFF)
            assignLead(user, leadMain, user, "");

     // Kafka is enabled, send the lead message
        if (leadKafkaProducer != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String leadJson = objectMapper.writeValueAsString(leadMain);
                leadKafkaProducer.sendLeadMessage(leadJson);
            } catch (Exception e) {
                logger.error("Error sending lead to Kafka", e);
            }
        } else {
            // Kafka is disabled, handle normal flow
            System.out.println("Kafka is disabled. Lead processing skipped.");
        }
    }
    return leadMain;
}
	
	private LeadMain updateLead(User loggedInUser, LeadMain changedLead) {

	    logger.debug("Updating lead {}", changedLead.getId());
	    Business business = getBusiness(loggedInUser, changedLead);

	    if (business == null) {
	        throw new RuntimeException("Not Allowed");
	    }

	    LeadMain original = leadMainRepository.findById(changedLead.getId())
	            .orElseThrow(() -> new RuntimeException("Lead not found"));

	    if (!business.equals(original.getBusiness())) {
	        throw new RuntimeException("Not Allowed");
	    }

	    if (changedLead.getName() == null || changedLead.getName().isEmpty()) {
	        throw new RuntimeException("Name is required");
	    }

	    // Check if the changedLead has LeadMainExtras and use them if available
	    LeadMainExtras originalExtras = original.getLeadMainExtas();
	    LeadMainExtras localExtras = changedLead.getLeadMainExtas();

	    if (localExtras != null) {
	        if (originalExtras == null) {
	            originalExtras = new LeadMainExtras();
	        }
	        originalExtras.setAboutUs(localExtras.getAboutUs());
	        originalExtras.setAddress(localExtras.getAddress());
	    }

	    // Check the role and update status if the user has ROLE_MERCHANT_ADMIN
	    if (loggedInUser.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN) {
	        original.setStatus(changedLead.getStatus());
	    }

	    // Update other properties
	    original.setName(changedLead.getName());
	    original.setPriority(changedLead.getPriority());
	    original.setComment(changedLead.getComment());
	    original.setCategory(changedLead.getCategory());
	    original.setLeadMainExtas(originalExtras);
	    original.setNextFollowUpTime(changedLead.getNextFollowUpTime());
	    original.setSource(changedLead.getSource());
	    original.setImportant(changedLead.isImportant());

	    original = leadMainRepository.save(original);

	    return original;
	}

	
	public Business getBusiness(User loggedInUser,LeadMain leadMain) {

		Business business = null;
		if (loggedInUser.getBusiness() != null)
			business = loggedInUser.getBusiness();
		else if (loggedInUser.getMerchant() != null)
			business = loggedInUser.getMerchant().getBusiness();

		return business;

	}
	
	public LeadMain assignLead(User loggedInUser , LeadMain lead, User assignee,String message) {
		
		if(lead == null)
			throw new RuntimeException("Lead Required");
		
		lead.setAssignedTo(assignee);
		lead = leadMainRepository.save(lead);
		
		if(assignee != null)
			leadActivityService.addAssigneeTrace(lead,loggedInUser,message);
		
		return lead;
	}
	
	
	public LeadMain getLead(Long LeadMainId) {
		
		if(LeadMainId == null)
			return null;
		
		System.out.println("---------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		LeadMain lead = leadMainRepository.findById(LeadMainId).get();
		
		return lead;
		
	}
	
	public void sortContactByPrimary(LeadMain leadMain) {

		if (leadMain == null || leadMain.getLeadMainExtas() == null
				|| leadMain.getLeadMainExtas().getContacts() == null)
			return;

		LeadMainExtras extras = leadMain.getLeadMainExtas();
		List<LeadContactOld> contacts = extras.getContacts();

		if (contacts.size() == 1)
			return;

		Integer primaryContactIndex = null;

		for (int i = 0; i < contacts.size(); i++) {

			LeadContactOld contact = contacts.get(i);

			if (contact.isPrimary()) {

				primaryContactIndex = i;
				break;

			}
		}

		if (primaryContactIndex == null || primaryContactIndex == 0)
			return;

		Collections.swap(contacts, 0, primaryContactIndex);

		extras.setContacts(contacts);

		leadMain.setLeadMainExtas(extras);

	}
	
	
	public LeadMain addContact(User loggedInUser,LeadMain lead, LeadContactOld newContact) {

		if (lead == null || newContact == null)
			return null;

		LeadMainExtras extras = lead.getLeadMainExtas();
		
		if(extras == null)
			extras = new LeadMainExtras();
	

		List<LeadContactOld> contacts = extras.getContacts();

//		if (contacts == null)
//			contacts = new ArrayList<LeadContactOld>();

		if (newContact.getContactID() == null || newContact.getContactID().isEmpty()) {
			// create
			newContact.setContactID(CodeUtils.getTimeStampID());
			contacts.add(newContact);

		} else {
			// update
			LeadContactOld original = null;

			if (contacts == null || contacts.size() == 0)
				throw new RuntimeException("Not Allowed");

			for (LeadContactOld leadContact : contacts) {
				if (newContact.getContactID().equals(leadContact.getContactID())) {
					original = leadContact;
				}
			}
			
			if (original == null)
				throw new RuntimeException("Invalid Contact Id");

			// update original instance except contactID
			original.setName(newContact.getName());
			original.setPhone(newContact.getPhone());
			original.setEmail(newContact.getEmail());
			original.setNote(newContact.getNote());
			original.setPrimary(newContact.isPrimary());
			newContact = original;
			
		}

		if (newContact.isPrimary())
			for (LeadContactOld contact : contacts) {
				if (!contact.getContactID().equals(newContact.getContactID())) {
					// Mark un primary other except itself
					contact.setPrimary(false);
				}
			}
		extras.setContacts(contacts);
		lead.setLeadMainExtas(extras);
		sortContactByPrimary(lead);
		return lead;
	}
	
	public LeadMain markPrimaryContact(User loggedInUser, LeadMain lead, String contactId) {

		Business business = getBusiness(loggedInUser,lead);
		LeadMainExtras extras = lead.getLeadMainExtas();

		if (extras == null || extras.getContacts() == null || extras.getContacts().size() == 0)
			throw new RuntimeException("Invalid Details");
		List<LeadContactOld> contacts = extras.getContacts();
		boolean isFound = false;
		for (LeadContactOld contact : contacts) {
			if (contact.getContactID().equals(contactId)) {
				contact.setPrimary(true);
				isFound = true;
				continue;
			}
			contact.setPrimary(false);
		}

		if (!isFound)
			throw new RuntimeException("Invalide Contact Sequence");

		extras.setContacts(contacts);
		lead.setLeadMainExtas(extras);
		sortContactByPrimary(lead);
		lead = leadMainRepository.save(lead);
		return lead;

	}
	
	public LeadActivity addActivity(User loggedInUser,Long leadMainId, LeadActivity newActivity) {

		Business business = getBusiness(loggedInUser);

		LeadMain lead = leadMainRepository.findById(leadMainId).get();

		boolean isStatusChanged = (lead.getStatus() != newActivity.getStatus());

		if (!isStatusChanged && (newActivity.getMessage() == null || newActivity.getMessage().isEmpty()))
			throw new RuntimeException("Both Empty");

		ActivityType activityStatus = null;
		lead.setStatus(newActivity.getStatus());
		lead = leadMainRepository.save(lead);
		
		LeadActivity activity = null;
		
		if (isStatusChanged) {
			//activityStatus = ActivityType.STATUS_CHANGED;
			activity = leadActivityService.addStatusChangeTrace(lead, loggedInUser, newActivity.getMessage());
		}
		else {
			//activityStatus = ActivityType.STATUS_UPDATE;
			activity = leadActivityService.addOrUpdateTaskTrace(loggedInUser, lead.getId(), activity);
		}
		
		//LeadActivity activity = preserveActivity(lead, activityStatus,newActivity.getMessage());

		return activity;

	}
	
	public LeadMain deleteContact(User user,Long leadID, String contactId) {
		LeadMain lead = leadMainRepository.findById(leadID).get();

		LeadMainExtras extras = lead.getLeadMainExtas();

		List<LeadContactOld> contacts = extras.getContacts();
		List<LeadContactOld> freshContacts = new ArrayList<>();

		for (LeadContactOld contact : contacts) {
			if (!contact.getContactID().equals(contactId)) {
				freshContacts.add(contact);
			}
		}
		extras.setContacts(freshContacts);
		lead.setLeadMainExtas(extras);
		lead = leadMainRepository.save(lead);

		return lead;
	}
	
	public Business getBusiness(User loggedInUser) {

		Business business = null;
		if (loggedInUser.getBusiness() != null)
			business = loggedInUser.getBusiness();
		else if (loggedInUser.getMerchant() != null)
			business = loggedInUser.getMerchant().getBusiness();

		return business;

	}
	
//	public List<String> getSource(Business business){
//		
//		List<String> defaultSources = new ArrayList<>(Arrays.asList(DEFAULT_LEAD_SOURCE));
//		
//		List<String> sources = leadMainRepository.findSourceByBusiness(business.getId());
//		
//		for(String defaultSource : defaultSources) {
//			if(!sources.contains(defaultSource))
//				sources.add(defaultSource);
//		}
//		
//		return sources;
//	}
	
	
	public List<String> getSource(Business business) {
        try {
            List<String> defaultSources = new ArrayList<>(Arrays.asList(DEFAULT_LEAD_SOURCE));

            List<String> sources = leadMainRepository.findSourceByBusiness(business.getId());

            for (String defaultSource : defaultSources) {
                if (!sources.contains(defaultSource))
                    sources.add(defaultSource);
            }

            return sources;
        } catch (Exception e) {
            logger.error("Error occurred while fetching lead sources", e);
            // Return an empty list or handle the error as per your application's requirement
            return Collections.emptyList();
        }
    }
	
	public LeadMain setImportantStatus(Long leadMainId) {
		
		LeadMain lead = leadMainRepository.findById(leadMainId).get();
		
		if(lead == null)
			new RuntimeException("Missing Lead : " + leadMainId);
		
		lead.setImportant(!lead.isImportant());
		lead = leadMainRepository.save(lead);
		
		return lead;
	}
	
}
