package com.SCM.leads;

import java.util.Arrays;

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
import com.SCM.leads.LeadActivity.ActivityStatis;
import com.SCM.leads.LeadActivity.ActivityType;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role.RoleType;
import com.SCM.utils.Utils;

@Service
public class LeadActivityService {
	
	@Autowired
	LeadActivityRepository leadActivityRepository;
	@Autowired
	LeadMainRepository leadMainRepository;
	@Autowired
	LeadContactRepository  leadContactRepositoty;
	@Autowired
	UserRepository userRepository;
	
//	@Autowired
//	LeadCommonService leadCommonService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public LeadActivity addOrUpdateTaskTrace(User user,Long leadID,LeadActivity leadActivity) {
		
		Business business =  user.getBusiness();
		
		if(user.getMerchant() != null)
			business = user.getMerchant().getBusiness();
		
		if(leadActivity.getActivityType() != ActivityType.TASK
				&& leadActivity.getActivityType() != ActivityType.TASK_CALL
				&& leadActivity.getActivityType() != ActivityType.TASK_EMAIL
				&& leadActivity.getActivityType() != ActivityType.TASK_MEETING) {
			
			throw new RuntimeException("Activity Can't be added/edited : " + leadActivity.getActivityType());
			
		}
		
		if(leadActivity.getId() == null) {
			// create
			LeadMain leadMain = leadMainRepository.findById(leadID).get();
			
			if(leadMain == null)
				throw new RuntimeException("Missing Lead : ");
			if(!leadMain.getBusiness().equals(business))
				throw new RuntimeException("Lead doesnot belong to business : " + business.getName());
			
			// contact
			if(leadActivity.getContact() != null) {
				if(!leadActivity.getContact().getLeadMain().equals(leadMain))
					throw new RuntimeException("Contact is not part of Lead ");
			}
			
			leadActivity.setLeadMain(leadMain);
			leadActivity.setStatus(leadMain.getStatus());
			return saveActivity(leadMain,leadActivity.getActivityType(),
					leadActivity.getActivityStatus(),
					user,
					leadActivity.getContact(),
					leadActivity.getMessage());	
		}
		
		// update
		LeadActivity original = leadActivityRepository.findById(leadActivity.getId()).get();
		
		if(!original.getLeadMain().getBusiness().equals(business))
			throw new RuntimeException("Activity Can't be changed, Not Allowed");
		
		if(leadActivity.getContact() != null) {
			if(!leadActivity.getContact().getLeadMain().equals(original.getLeadMain()))
				throw new RuntimeException("Contact is not part of Lead ");
			original.setContact(leadActivity.getContact());
		}
		
		// update Activity Status and comment
		original.setActivityStatus(leadActivity.getActivityStatus());
		original.setMessage(leadActivity.getMessage());
		return leadActivityRepository.save(original);
		
	}
	
	public LeadActivity addAssigneeTrace(LeadMain lead,User createdBy,String message ) {
		
		return saveActivity(lead,ActivityType.ASSIGNED,null,createdBy,message);
		
	}
	
	public LeadActivity addStatusChangeTrace(LeadMain lead,User createdBy,String message) {
		return saveActivity(lead,ActivityType.STATUS_CHANGED,null,createdBy,message);
	}
	
	public LeadActivity saveActivity(LeadMain lead, ActivityType activityType, ActivityStatis activityStatis, User createdBy,String message) {
		return saveActivity(lead,activityType,activityStatis,createdBy,null,message);
	}
	
	public LeadActivity saveActivity(LeadMain lead, ActivityType activityType, ActivityStatis activityStatis, User createdBy,LeadContact leadContact,String message) {
		
		LeadActivity activity = new LeadActivity();
		
		System.out.println("contact :>>>>>>>>>>>>>>>>>>>>>>>>>> " + leadContact);
		
		activity.setLeadMain(lead);
		activity.setStatus(lead.getStatus());
		activity.setActivityType(activityType);
		activity.setActivityStatus(activityStatis);
		activity.setAssignedTo(lead.getAssignedTo());
		activity.setContact(leadContact);
		activity.setCreatedBy(createdBy);
		activity.setMessage(message);
		activity = leadActivityRepository.save(activity);
		
		return activity;
		
	}
	
	public LeadActivity getTaskActivity(Long activityID,User user) {
		
		Business business = null;
		User assignee = null;
		
		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN)
			business = user.getBusiness();
		else if(user.getRole().getName() == RoleType.ROLE_MERCHANT_STAFF) {
			
			business = user.getMerchant().getBusiness();
			assignee = user;
		}
		
		return leadActivityRepository.findByBusinessAndAssignee(activityID, 
				business.getId(), 
				assignee != null ? assignee.getId() : null, 
				Arrays.asList(ActivityType.TASK,ActivityType.TASK_EMAIL,
						ActivityType.TASK_CALL,ActivityType.TASK_MEETING));
	}
	
	public Page<LeadActivity> getLeadActivities(User user,Pageable pageable, ActivityType activityType,
			String message,String leadName,String leadID,String assignee,
			String creator,
			Long contactID,
			String startDate,
			String endDate){
		
		

		logger.debug("Get LeadAcitivy cPage Wise...");
		
		Sort sort = pageable.getSort();

		if (sort == null) {
			sort = Sort.by(Sort.Direction.DESC, "creationTime");
		}
		
		if(endDate != null && startDate == null)
			startDate = "1970-01-01";
		if(startDate != null && endDate == null)
			endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
		
		Business business = null;
		User assigneedTo = null;
		
		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN) {
			
			business = user.getBusiness();
			
			if(assignee != null) {
				assigneedTo = userRepository.findByEmail(assignee).get();
				if(assigneedTo.getMerchant() == null)
					throw new RuntimeException("Not Allowed");
				if(!assigneedTo.getMerchant().getBusiness().equals(business))
					throw new RuntimeException("Not Allowed");
			}
			
		}else if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN) {
			business = user.getMerchant().getBusiness();
			assigneedTo = user;
			
			// Filter By Assignee and Creator is not allowed
			
			assignee = null;
			creator = null;
		
		}
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		
		Page<LeadActivity> leadActivity = leadActivityRepository.findLeadActivityPageWise(pageRequest,
				business.getId(),
				assigneedTo != null ? assigneedTo.getId() : null,
				activityType,
				message,
				leadName,
				leadID,
				creator,
				contactID,
				startDate,
				endDate);
				
	
		return leadActivity;
	}
}
