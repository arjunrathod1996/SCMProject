package com.SCM.leads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.role.Role.RoleType;
import com.SCM.utils.CodeUtils;
import com.SCM.utils.Utils;

@Service
public class LeadContactService {

	@Autowired
	LeadContactRepository leadContactRepositoty;
	@Autowired
	LeadMainRepository leadMainRepository;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public LeadContact createOrUpdate(User user , Long leadID,LeadContact leadContact) {
		Business business = null;
		business = user.getBusiness();
		
		if(user.getMerchant() != null)
			business = user.getMerchant().getBusiness();
		
		if(leadContact.getId() == null) {
			// create
			LeadMain leadMain = leadMainRepository.findById(leadID).get();
			
			if(leadMain == null)
				throw new RuntimeException("Missing Lead");
			
			leadContact.setLeadMain(leadMain);
			leadContact.setBusiness(business);
			leadContact.setContactID(CodeUtils.getTimeStampID());
			leadContact.setUpdatedBy(user);
			leadContact.setCreatedBy(user);
			return leadContactRepositoty.save(leadContact);
		}
			
		// update
	
		LeadContact original = leadContactRepositoty.findById(leadContact.getId()).get();
	
		if(original == null)
			throw new RuntimeException("No Contact Found: " + leadContact.getId());
		if(!original.getBusiness().equals(business))
			throw new RuntimeException("Contact Can't be Changed , Not Allowed");
		
		original.setFirstName(leadContact.getFirstName());
		original.setLastName(leadContact.getLastName());
		original.setPhoneNumber(leadContact.getPhoneNumber());
		original.setPhoneNumber2(leadContact.getPhoneNumber2());
		original.setEmail(leadContact.getEmail());
		original.setDesignation(leadContact.getDesignation());
		original.setImportant(leadContact.isImportant());
		original.setLocation(leadContact.getLocation());
		original.setNote(leadContact.getNote());
		original.setUpdatedBy(leadContact.getUpdatedBy());
		original.setContactExtras(leadContact.getContactExtras());
		
		return leadContactRepositoty.save(original);
	}
	
	public LeadContact getContact(Long contactID,User user) {
		
		Business business = null;
		User assignee = null;
		
		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN)
			business = user.getBusiness();
		else if(user.getRole().getName() == RoleType.ROLE_MERCHANT_STAFF) {
			business = user.getMerchant().getBusiness();
			assignee = user;
		}
			
		return leadContactRepositoty.findByBusinessAndAsignee(contactID, business.getId(), assignee != null ? assignee.getId() : null);
		
		
	}
	
	public Page<LeadContact> getLeadContacts(User user,Pageable pageable,String name,String email,String phoneNumber,
			String designation,String leadName,String leadID, String creator,Boolean important,String startDate , String endDate){
		
		logger.debug("Get LeadContacts Page Wise...");
		
		Sort sort = pageable.getSort();

		if (sort == null) {
			sort = Sort.by(Sort.Direction.DESC, "creationTime");
		}
		
		if(endDate != null && startDate == null)
			startDate = "1970-01-01";
		if(startDate != null && endDate == null)
			endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
		
		Business business = null;
		User assignee = null;
		
		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN)
			business = user.getBusiness();
		else if(user.getRole().getName() == RoleType.ROLE_MERCHANT_STAFF) {
			business = user.getMerchant().getBusiness();
			assignee = user;
		}
		
		
		
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
	
		Page<LeadContact> leadContacts = leadContactRepositoty.findLeadContactPageWise(pageRequest,
				business.getId(),
				assignee != null ? assignee.getId() : null,
				name,
				email,
				phoneNumber,
				designation,
				leadName,
				leadID,
				creator,
				important,
				startDate,
				endDate
						);
	
		return leadContacts;
		
	}
}
