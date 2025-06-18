package com.SCM.referral;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.relation.CustomerRelation;
import com.SCM.relation.CustomerRelationRepository;
import com.SCM.repository.BusinessRepository;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.service.CommonService;
import com.SCM.service.CustomerService;

@RestController
@RequestMapping("/api/referrals")
public class ReferralController {
	
	@Autowired
	CommonService commonService;
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	CustomerRelationRepository customerRelationRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	ReferralService referralService;
	@Autowired
	CustomerService customerService;
	
	private static final Logger logger = LoggerFactory.getLogger(ReferralController.class);
	
	//@RequestMapping(value = "/multipleContacts", method = RequestMethod.POST)
	@RequestMapping(value = "/multipleContacts/{shortLink}/{staffLink}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> multipleContacts(@RequestBody Map<String, List<Map<String, Object>>> requestBody,
                                              @RequestParam(value = "merchantID", required = false) Long merchantID,
                                              @PathVariable String shortLink, @PathVariable String staffLink) {

        try {
            // Access the "selectedContacts" list from the request body
            List<Map<String, Object>> selectedFrontEnd = requestBody.get("selectedContacts");
            if (selectedFrontEnd == null) {
                logger.error("selectedContacts list is missing in the request body");
                return new ResponseEntity<>("selectedContacts list is required", HttpStatus.BAD_REQUEST);
            }

            List<Map<String, Object>> selected = new ArrayList<>();
            Set<String> uniqueMobiles = new HashSet<>(); // To keep track of unique mobile numbers

            for (Map<String, Object> contact : selectedFrontEnd) {
                String mobile = (String) contact.get("mobile");
                if (mobile != null && !uniqueMobiles.contains(mobile)) {
                    selected.add(contact);
                    uniqueMobiles.add(mobile);
                }
            }

            // Count of newly referred contacts
            int newContactsCount = 0;

            // Retrieve the logged-in user
            User user = commonService.getLoggedInUser();
            if (user == null) {
                logger.error("No logged-in user found");
                return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
            }

            // Get the referrer customer associated with the user
            Customer referrer = user.getCustomer();
            if (referrer == null) {
                logger.error("No customer found for the logged-in user");
                return new ResponseEntity<>("Customer not found for the logged-in user", HttpStatus.NOT_FOUND);
            }

            //Merchant merchant = merchantRepository.findById(merchantID).orElse(null);
            Merchant merchant = merchantRepository.findByShortLink(shortLink);
            if (merchant == null) {
                logger.error("Merchant not found for ID: {}", merchantID);
                return new ResponseEntity<>("Merchant not found", HttpStatus.NOT_FOUND);
            }

            Business business = businessRepository.findById(merchant.getId()).orElse(null);
            if (business == null) {
                logger.error("Business not found for ID: {}", merchantID);
                return new ResponseEntity<>("Business not found", HttpStatus.NOT_FOUND);
            }

            List<String> mobileNumbers = new ArrayList<>();

            for (Map<String, Object> contact : selected) {
                String mobileNumber = (String) contact.get("mobile");
                String name = (String) contact.get("name");

                if (mobileNumber != null && name != null && !containsDigit(name)) {
                    logger.info("Processing contact - name: {}, mobile: {}", name, mobileNumber);
                    mobileNumbers.add(mobileNumber);
                }
            }

            List<CustomerRelation> existingCustomers = customerRelationRepository
                    .findCustomerRelationsByMerchantAndBusinessAndMobileNumbers(merchant.getId(), business.getId(), mobileNumbers);

            List<String> existingCustomerMobileNumbers = new ArrayList<>();
            for (CustomerRelation customerRelation : existingCustomers) {
                existingCustomerMobileNumbers.add(customerRelation.getCustomer().getMobileNumber());
            }

            List<Map<String, Object>> newContacts = new ArrayList<>();
            for (Map<String, Object> contact : selected) {
                String mobileNumber = (String) contact.get("mobile");
                String name = (String) contact.get("name");

                if (mobileNumber != null && name != null && !containsDigit(name)
                        && !mobileNumber.equals(referrer.getMobileNumber())
                        && !existingCustomerMobileNumbers.contains(mobileNumber)) {
                    newContactsCount++;
                    newContacts.add(contact);
                }
            }

            logger.info("New Contacts: {}", newContacts);

            for (Map<String, Object> contact : newContacts) {
                String mobileNumber = (String) contact.get("mobile");
                String contactName = (String) contact.get("name");
                String email = (String) contact.get("email");

                String firstName = null;
                String lastName = null;
                if (contactName != null) {
                    String[] nameParts = contactName.split(" ");
                    firstName = nameParts.length > 0 ? nameParts[0] : null;
                    lastName = nameParts.length > 1 ? nameParts[1] : null;
                }

                List<Customer> customers = customerRepository.findByMobileNumber(mobileNumber);
                Customer referred;
                if (customers != null && !customers.isEmpty()) {
                    referred = customers.get(0);
                } else {
                    referred = new Customer();
                    referred.setMobileNumber(mobileNumber);
                    referred.setFirstName(firstName);
                    referred.setLastName(lastName);
                    referred.setEmail(email);
                    referred = customerService.save(referred, merchant);
                }

                try {
                    Refferal referral = new Refferal();
                    referral.setMerchant(merchant);
                    referral.setReferrer(referrer);
                    referral.setReferred(referred);
                    referralService.create(user, referral);
                } catch (Exception ex) {
                    logger.error("Error creating referral for contact: {}", contact, ex);
                    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }

            String responseMessage = "Successfully referred " + newContactsCount + " new contacts.";
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);

        } catch (Exception ex) {
            logger.error("Unexpected error occurred while processing contacts", ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	// Method to check if a string contains a digit
		public static boolean containsDigit(String s) {
		    for (char c : s.toCharArray()) {
		        if (Character.isDigit(c)) {
		            return true;
		        }
		    }
		    return false;
		}

}
