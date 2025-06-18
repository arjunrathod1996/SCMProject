package com.SCM.leads;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.leads.LeadMain.Priority;
import com.SCM.leads.LeadMain.Status;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role;
import com.SCM.role.Role.RoleType;
import com.SCM.role.RoleRepository;
import com.SCM.role.RoleService;
import com.SCM.service.CommonService;


import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/user/dashboard/leads")
@Controller
public class LeadMainViewController {

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
	RoleRepository roleRepository;
	@Autowired
	LeadContactRepository leadContactRepositoty;
	@Autowired
	RoleService roleService;

	private Logger logger = LoggerFactory.getLogger(LeadMainViewController.class);
	
//	@RequestMapping(value = "",method = RequestMethod.GET)
//	public String leads(Model model , HttpServletRequest request,
//			@RequestParam(value = "important",required = false) Boolean important,
//			@RequestParam(value = "priority",required = false) Priority priority,
//			@RequestParam(value = "assignedTo",required = false) Integer assignedTo,
//			@RequestParam(value = "status",required = false) Status status,
//			@RequestParam(value = "nextFollowUp",required = false) String nextFollowUp) {
//
//		User user = commonService.getLoggedInUser();
//		
//		Business business = leadMainService.getBusiness(user);
//		
//		List<Merchant> merchants = merchantRepository.findByBusiness(business);
//		
//		List<String> leadSources = null;
//		
//		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN) {
//			leadSources = leadMainService.getSource(user.getMerchant().getBusiness());
//			model.addAttribute("allowUpdateLeadInfo",true);
//			
//			//List<Role> roles  = roleRepository.findByRoleName(RoleType.ROLE_MERCHANT_STAFF);
//			
//			//List<Role> roles = roleRepository.findByRoleName(Role.RoleType.ROLE_MERCHANT_STAFF);
//			
//			
//			 List<Role> roles = roleService.getRolesByRoleName(RoleType.ROLE_MERCHANT_STAFF);
//		        roles.forEach(role -> System.out.println(role));
//			
//			System.out.println("Query executed. Roles found: " + roles);
//
//			
//			System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>  :" + roles);
//
//			Role role = roles.get(0);
//			
//			System.out.println(" >>>>>>>>>>>>>>>>>>>>> : " + role);
//			
//			List<User> users = userRepository.findByMerchantInAndRole(merchants,role);
//			
//			System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : " + users);
//			
//			merchants = merchantRepository.findByBusiness(business);
//			
//			model.addAttribute("users",users);
//			
//		}else {
//			
//			// Assigned Leads to merchants/Staffs
//			
//			leadSources = leadMainService.getSource(user.getMerchant().getBusiness());
//			merchants = Arrays.asList(user.getMerchant());
//			model.addAttribute("assigneeView",true);
//		}
//		
//		model.addAttribute("leadSources",leadSources);
//		model.addAttribute("filterStatus",status);
//		model.addAttribute("filterAssignee",assignedTo);
//		model.addAttribute("filterNextFollowUp",nextFollowUp);
//		model.addAttribute("categories",leadMainRepository.findCategoryBusiness(business.getId()));
//	
//		return "user/leads";
//	
//	}
	
	
//	@RequestMapping(value = "",method = RequestMethod.GET)
//	public String leads(Model model , HttpServletRequest request,
//			@RequestParam(value = "important",required = false) Boolean important,
//			@RequestParam(value = "priority",required = false) Priority priority,
//			@RequestParam(value = "assignedTo",required = false) Integer assignedTo,
//			@RequestParam(value = "status",required = false) Status status,
//			@RequestParam(value = "nextFollowUp",required = false) String nextFollowUp) {
//
//		User user = commonService.getLoggedInUser();
//		
//		Business business = leadMainService.getBusiness(user);
//		
//		List<Merchant> merchants = merchantRepository.findByBusiness(business);
//		
//		List<String> leadSources = null;
//		
//		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN) {
//			leadSources = leadMainService.getSource(user.getMerchant().getBusiness());
//			model.addAttribute("allowUpdateLeadInfo",true);
//			
//			//List<Role> roles  = roleRepository.findByRoleName(RoleType.ROLE_MERCHANT_STAFF);
//			
//			//List<Role> roles = roleRepository.findByRoleName(Role.RoleType.ROLE_MERCHANT_STAFF);
//			
//			
//			 List<Role> roles = roleService.getRolesByRoleName(RoleType.ROLE_MERCHANT_STAFF);
//		        roles.forEach(role -> System.out.println(role));
//			
//			System.out.println("Query executed. Roles found: " + roles);
//
//			
//			System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>  :" + roles);
//
//			Role role = roles.get(0);
//			
//			System.out.println(" >>>>>>>>>>>>>>>>>>>>> : " + role);
//			
//			List<User> users = userRepository.findByMerchantInAndRole(merchants,role);
//			
//			for(User u : users) {
//				
//				System.out.println(" >>>>>>>>>>>>>>>>>>>>>> : " + u.getBusiness().getId());
//				
//			}
//			
//			
//			System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : " + users);
//			
//			merchants = merchantRepository.findByBusiness(business);
//			
//			model.addAttribute("users",users);
//			
//		}else {
//			
//			// Assigned Leads to merchants/Staffs
//			
//			leadSources = leadMainService.getSource(user.getMerchant().getBusiness());
//			merchants = Arrays.asList(user.getMerchant());
//			model.addAttribute("assigneeView",true);
//		}
//		
//		model.addAttribute("leadSources",leadSources);
//		model.addAttribute("filterStatus",status);
//		model.addAttribute("filterAssignee",assignedTo);
//		model.addAttribute("filterNextFollowUp",nextFollowUp);
//		model.addAttribute("categories",leadMainRepository.findCategoryBusiness(business.getId()));
//	
//		return "user/leads";
//	
//	}
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
    public String leads(Model model, HttpServletRequest request,
                        @RequestParam(value = "important", required = false) Boolean important,
                        @RequestParam(value = "priority", required = false) Priority priority,
                        @RequestParam(value = "assignedTo", required = false) Integer assignedTo,
                        @RequestParam(value = "status", required = false) Status status,
                        @RequestParam(value = "nextFollowUp", required = false) String nextFollowUp) {
        try {
            User user = commonService.getLoggedInUser();

            Business business = leadMainService.getBusiness(user);
            List<Merchant> merchants = merchantRepository.findByBusiness(business);
            List<String> leadSources = null;

            if (RoleType.ROLE_MERCHANT_ADMIN.equals(user.getRole().getName())) {
                leadSources = leadMainService.getSource(user.getBusiness());
                model.addAttribute("allowUpdateLeadInfo", true);

                List<Role> roles = roleService.getRolesByRoleName(RoleType.ROLE_MERCHANT_STAFF);
                logger.debug("Query executed. Roles found: {}", roles);

                if (!roles.isEmpty()) {
                    Role role = roles.get(0);
                    List<User> users = userRepository.findByMerchantInAndRole(merchants, role);
                    logger.debug("Users found: {}", users);

                    model.addAttribute("users", users);
                }

            } else {
                leadSources = leadMainService.getSource(user.getMerchant().getBusiness());
                merchants = Arrays.asList(user.getMerchant());
                model.addAttribute("assigneeView", true);
            }

            model.addAttribute("leadSources", leadSources);
            model.addAttribute("filterStatus", status);
            model.addAttribute("filterAssignee", assignedTo);
            model.addAttribute("filterNextFollowUp", nextFollowUp);
            model.addAttribute("categories", leadMainRepository.findCategoryBusiness(business.getId()));

            return "user/leads";

        } catch (Exception e) {
            logger.error("Error in leads method", e);
            // Handle the exception gracefully and return an appropriate error page or message
            return "error-page";
        }
    }
	
	@RequestMapping(value = "/conatcts",method = RequestMethod.GET)
	public String getLeadContacts( Model model,
			@RequestParam(value = "leadID",required = false, defaultValue = "") String leadID) {
		
		
		
		User user = commonService.getLoggedInUser();
		
		if(leadID != null) {
			List<LeadMain> leads = leadMainRepository.findByLeadIDAndDeleted(leadID, false);
			if(leads != null && leads.size() > 0) {
				LeadMain lead = leads.get(0);
				model.addAttribute("lead",lead);
			}
		}
			
		return "user/leads_contact";
	}
	
	@RequestMapping(value = "/activity", method = RequestMethod.GET)
	public String getLeadActivities(
	    Model model,
	    @RequestParam(value = "leadID", required = false) String leadID,
	    @RequestParam(value = "conatctID", required = false) Long conatctID) {

	

	    if (leadID != null) {
	        List<LeadMain> leads = leadMainRepository.findByLeadIDAndDeleted(leadID, false);
	        if (!leads.isEmpty()) {
	            LeadMain lead = leads.get(0);
	            model.addAttribute("lead", lead);
	        }
	    } else if (conatctID != null) { // Check if conatctID is not null
	        Optional<LeadContact> contactOptional = leadContactRepositoty.findById(conatctID);
	        if (contactOptional.isPresent()) {
	            LeadContact contact = contactOptional.get();
	            model.addAttribute("lead", contact.getLeadMain());
	            model.addAttribute("contact", contact);
	        } else {
	            // Handle the case where the contact with the given ID was not found.
	            // You can add appropriate error handling logic here.
	        }
	    }

	    return "user/leads_activity";
	}

	
}

