package com.SCM.leads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.SCM.leads.LeadMain.Priority;
import com.SCM.leads.LeadMain.Status;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role;
import com.SCM.role.Role.RoleType;
import com.SCM.role.RoleRepository;
import com.SCM.service.UserService;
import com.SCM.service.UserServiceImpl;
import com.SCM.utils.Utils;

@Service
public class LeadMainReportService {
	
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";
	
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	LeadMainRepository leadMainRepository;
	@Autowired
	LeadActivityRepository leadActivityRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	LeadContactRepository leadContactRepositoty;
	@Autowired
	LeadMainService leadMainService;
	@Autowired
	UserServiceImpl userService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
//	public Page<LeadMain> getLeadList(User user, Pageable pageable, String leadName,
//			String leadID, Long assigneeID, Boolean important, Priority priority,
//			Status status,String followUpStartDate,String followUpEndDate,
//			String startDate, String endDate){
//		
//		logger.debug("Get LeadMain Page Wise...");
//		
//		Sort sort = pageable.getSort();
//		if (sort == null) {
//			sort = Sort.by(Sort.Direction.DESC, "creationTime");
//		}
//		
//		
//		
//		if(endDate != null && startDate == null)
//			startDate = "1970-01-01";
//		if(startDate != null && endDate == null)
//			endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
//		
//		// consider only fiven date
//		if(followUpEndDate != null) {
//			startDate = followUpEndDate;
//			endDate = followUpEndDate;
//		}
//		
//		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//		
//		Business business = null;
//		
//		if(user.getRole().getName() == Role.RoleType.ROLE_MERCHANT_ADMIN) {
//			
//			System.out.println(">>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< : " + user.getRoleName());
//			business = user.getBusiness();
//		}
//		
//		if(user.getRole().getName() == Role.RoleType.ROLE_MERCHANT_STAFF) {
//			
//			System.out.println(">>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<< : " + user.getRoleName());
//			
//			business = user.getMerchant().getBusiness();
//			
//			System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<< : " + business.getId());
//		}
//		
//		
//		User assignee = null;
//		
//		if(assigneeID != null) {
//			if(user.getRole().getName() != RoleType.ROLE_MERCHANT_ADMIN)
//				throw new RuntimeException("Not Allowed");
//		}
//		
//		System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : " + business.getId());
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : " + assigneeID);
//		
//		//Page<LeadMain> leads = leadMainRepository.findLeadsPageWise(pageRequest);
//		
//		Page<LeadMain> leads = leadMainRepository.findLeadsPageWise(pageRequest,
//				business.getId(),
//				assignee != null ? assignee.getId() : null,
//				status,
//				priority,
//				important,
//				leadName,
//				leadID,
//				followUpStartDate,
//				followUpEndDate,
//				startDate,
//				endDate);
//		
//		// Include Contact count
//		includeContactCount(leads.getContent());
//		
//		for(LeadMain l : leads) {
//			l.setStatusStyle(l.getStatus().getLabelClass());
//			l.setPriorityStyle(l.getPriority().getLabelClass());
//		}
//		
//		return leads;
//		
//	}
//	
//	private void includeContactCount(List<LeadMain> leads) {
//		
//		if(leads.size() <= 0) {
//			return;
//		}
//		
//		Map<Long, LeadMain> leadMap = new HashMap<>();
//		Business business  = null;
//		
//		for(LeadMain lead : leads) {
//			business = lead.getBusiness();
//			leadMap.put(lead.getId(),lead);
//		}
//		
//		List<Object[]> records = leadContactRepositoty.countByLeadMain(business.getId(), new ArrayList<Long>(leadMap.keySet()));
//		
//		for(Object[] r : records) {
//			Long id = ((Number) r[0]).longValue();
//			Integer count = ((Number) r[1]).intValue();
//			
//			LeadMain lead = leadMap.get(id);
//			lead.setContactCount(count);
//		}
//	}
	
	
//	 public Page<LeadMain> getLeadList(User user, Pageable pageable, String leadName,
//             String leadID, Long assigneeID, Boolean important, Priority priority,
//             Status status, String followUpStartDate, String followUpEndDate,
//             String startDate, String endDate) {
//
//			logger.debug("Get LeadMain Page Wise...");
//			
//			Sort sort = pageable.getSort();
//			if (sort == null) {
//			sort = Sort.by(Sort.Direction.DESC, "creationTime");
//			}
//			
//			if (endDate != null && startDate == null) startDate = "1970-01-01";
//			if (startDate != null && endDate == null) endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
//			
//			// Consider only given date
//			if (followUpEndDate != null) {
//			startDate = followUpEndDate;
//			endDate = followUpEndDate;
//			}
//			
//			PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//			
//			Business business = null;
//			
//			try {
//			if (Role.RoleType.ROLE_MERCHANT_ADMIN.equals(user.getRole().getName())) {
//				logger.debug("User is a MERCHANT_ADMIN");
//				business = user.getBusiness();
//			} else if (Role.RoleType.ROLE_MERCHANT_STAFF.equals(user.getRole().getName())) {
//				logger.debug("User is a MERCHANT_STAFF");
//				business = user.getMerchant().getBusiness();
//			} else {
//				logger.warn("User has an unknown role: {}", user.getRole().getName());
//			}
//			
//			if (business == null) {
//				throw new RuntimeException("Business not found for user.");
//			}
//			
//			User assignee = null;
//			if (assigneeID != null) {
//			if (!Role.RoleType.ROLE_MERCHANT_ADMIN.equals(user.getRole().getName())) {
//			throw new RuntimeException("Not Allowed");
//			}
//			// Assignee logic here (e.g., fetch the assignee by ID)
//				//assignee = userService.findUserById(assigneeID); // Example service call
//			}
//			
//			logger.debug("Fetching leads for business ID: {}", business.getId());
//			Page<LeadMain> leads = leadMainRepository.findLeadsPageWise(pageRequest,
//			business.getId(),
//			assignee != null ? assignee.getId() : null,
//			status,
//			priority,
//			important,
//			leadName,
//			leadID,
//			followUpStartDate,
//			followUpEndDate,
//			startDate,
//			endDate);
//			
//			includeContactCount(leads.getContent());
//			
//			for (LeadMain l : leads) {
//				l.setStatusStyle(l.getStatus().getLabelClass());
//				l.setPriorityStyle(l.getPriority().getLabelClass());
//			}
//			
//			return leads;
//			
//			} catch (Exception e) {
//					logger.error("Error fetching lead list", e);
//					throw new RuntimeException("Error fetching lead list", e);
//				}
//			}
//			
//				private void includeContactCount(List<LeadMain> leads) {
//					if (leads.isEmpty()) {
//					return;
//				}
//			
//				Map<Long, LeadMain> leadMap = new HashMap<>();
//				Business business = null;
//			
//				for (LeadMain lead : leads) {
//					business = lead.getBusiness();
//					leadMap.put(lead.getId(), lead);
//				}
//			
//				if (business != null) {
//				List<Object[]> records = leadContactRepositoty.countByLeadMain(business.getId(), new ArrayList<>(leadMap.keySet()));
//				
//				for (Object[] r : records) {
//				Long id = ((Number) r[0]).longValue();
//				Integer count = ((Number) r[1]).intValue();
//			
//				LeadMain lead = leadMap.get(id);
//				lead.setContactCount(count);
//			}
//		}
//	}
	
	public Page<LeadMain> getLeadList(User user, Pageable pageable, String leadName,
            String leadID, Long assigneeID, Boolean important, Priority priority,
            Status status, String followUpStartDate, String followUpEndDate,
            String startDate, String endDate) {

				logger.debug("Get LeadMain Page Wise...");
				
				Sort sort = pageable.getSort();
				if (sort == null) {
				sort = Sort.by(Sort.Direction.DESC, "creationTime");
				}
				
				if (endDate != null && startDate == null) startDate = "1970-01-01";
				if (startDate != null && endDate == null) endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
				
				// Consider only given date
				if (followUpEndDate != null) {
				startDate = followUpEndDate;
				endDate = followUpEndDate;
					}
				
				PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
				
				Business business = null;
				Long creatorId = null;
				
				try {
				if (Role.RoleType.ROLE_MERCHANT_ADMIN.equals(user.getRole().getName())) {
				logger.debug("User is a MERCHANT_ADMIN");
				business = user.getBusiness();
					} else if (Role.RoleType.ROLE_MERCHANT_STAFF.equals(user.getRole().getName())) {
				logger.debug("User is a MERCHANT_STAFF");
				business = user.getMerchant().getBusiness();
				creatorId = user.getId(); // Set creatorId to the logged-in user's ID
					} else {
				logger.warn("User has an unknown role: {}", user.getRole().getName());
				}
				
				if (business == null) {
				throw new RuntimeException("Business not found for user.");
					}
				
				User assignee = null;
				if (assigneeID != null) {
				if (!Role.RoleType.ROLE_MERCHANT_ADMIN.equals(user.getRole().getName())) {
				throw new RuntimeException("Not Allowed");
					}
				// Assignee logic here (e.g., fetch the assignee by ID)
				assignee = userService.findUserById(assigneeID); // Example service call
				}
				
				logger.debug("Fetching leads for business ID: {}", business.getId());
				Page<LeadMain> leads = leadMainRepository.findLeadsPageWise(pageRequest,
				business.getId(),
				assignee != null ? assignee.getId() : null,
				creatorId, // Pass creatorId to the repository method
				status,
				priority,
				important,
				leadName,
				leadID,
				followUpStartDate,
				followUpEndDate,
				startDate,
				endDate);
				
				includeContactCount(leads.getContent());
				
				for (LeadMain l : leads) {
				l.setStatusStyle(l.getStatus().getLabelClass());
				l.setPriorityStyle(l.getPriority().getLabelClass());
					}
				
				return leads;
				
				} catch (Exception e) {
					logger.error("Error fetching lead list", e);
					throw new RuntimeException("Error fetching lead list", e);
					}
				}
				
				private void includeContactCount(List<LeadMain> leads) {
					if (leads.isEmpty()) {
					return;
				}
				
				Map<Long, LeadMain> leadMap = new HashMap<>();
				Business business = null;
				
				for (LeadMain lead : leads) {
					business = lead.getBusiness();
					leadMap.put(lead.getId(), lead);
				}
				
				if (business != null) {
				List<Object[]> records = leadContactRepositoty.countByLeadMain(business.getId(), new ArrayList<>(leadMap.keySet()));
				
				for (Object[] r : records) {
				Long id = ((Number) r[0]).longValue();
				Integer count = ((Number) r[1]).intValue();
				
				LeadMain lead = leadMap.get(id);
				lead.setContactCount(count);
			}
		}
	}

}
