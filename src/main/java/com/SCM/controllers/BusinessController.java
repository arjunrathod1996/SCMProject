package com.SCM.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SCM.businessConfig.Config;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.repository.BusinessRepository;
import com.SCM.role.Role.RoleType;
import com.SCM.service.BusinessService;
import com.SCM.service.CommonService;
import com.SCM.service.MerchantService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BusinessController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

    @Autowired
    BusinessService businessService;
    @Autowired
    MerchantService merchantService;
    @Autowired
    BusinessRepository businessRepository;
    @Autowired
    CommonService commonService;

    @PostMapping("/business")
    public ResponseEntity<Business> saveBusiness(@RequestBody Business business, @RequestParam(value = "id", required = false) Long id) {
        logger.info("Received request to save business with ID: {}", id);
        if (business == null) {
            logger.error("Invalid input received. Request body is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id != null) {
            business.setId(id);
            logger.info("Received business ID as a parameter: {}", id);
        }

        Business savedBusiness = businessService.saveBusiness(business);
        if (savedBusiness != null) {
            logger.info("Business saved successfully with ID: {}", savedBusiness.getId());
            return new ResponseEntity<>(savedBusiness, HttpStatus.CREATED);
        } else {
            logger.error("Failed to save business.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/businessPageWise", method = RequestMethod.GET)
    @ResponseBody
    public Page<?> getBusinessPageWise(Pageable pageable,
                                           @RequestParam(value = "address", required = false) String address,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate) {
        logger.info("Received request to fetch businesses page-wise.");
        logger.debug("Received parameters: name={}, address={}, startDate={}, endDate={}", name, address, startDate, endDate);
        return businessService.getBusiness(pageable);
    }
    
    @GetMapping("/business/delete")
	@ResponseBody
	public ResponseEntity<?> deleteBusiness(@RequestParam(value = "id")Long id){
    	businessService.deleteBusiness(id);
    	logger.debug("Deleted Business : {} ",id);
		return new ResponseEntity<String>("{}",HttpStatus.OK);
		
	}

    @RequestMapping(value = "/business", method = RequestMethod.GET)
    public ResponseEntity<?> getBusinessById(@RequestParam Long id) {
        logger.info("Received request to fetch business with ID: {}", id);
        try {
            Optional<Business> business = businessService.findByUserId(id);
            if (business.isPresent()) {
                logger.info("Business found with ID: {}", id);
                return ResponseEntity.ok(business.get());
            } else {
                logger.warn("No business found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching business data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching business data");
        }
    }
    
//    @RequestMapping(value="/business/search", method = RequestMethod.GET)
//   	public List<Business> searchCity(@RequestParam(value = "name", required = false) String name){
//    	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  : " + name);
//   		List<Business> business = null;
//   		business = businessRepository.findByName(name);
//   		return business;
//   	}
    @RequestMapping(value = "/business/search", method = RequestMethod.GET)
    public ResponseEntity<List<Business>> searchCity(@RequestParam(value = "name") String name) {
        logger.info("Business found with name: {}", name);
        System.out.println("Searching for business with name: " + name);
        List<Business> businesses = businessRepository.findByName(name);
        if (businesses == null || businesses.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no businesses are found
        }
        return ResponseEntity.ok(businesses); // Return 200 OK with the list of businesses
    }
    
    @RequestMapping(value = "/business/config", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateConfig(@RequestBody Config changedConfig, @RequestParam(value = "businessID") Long businessID) {
        try {
            User user = commonService.getLoggedInUser();

            if (!user.getRole().getName().equals(RoleType.ROLE_ADMIN)) {
                logger.warn("User {} with role {} attempted to update config without permission", user.getFirstName(), user.getRole().getName());
                return new ResponseEntity<>("Not Allowed", HttpStatus.BAD_REQUEST);
            }

            Business business = businessRepository.findById(businessID).orElse(null);

            if (business == null) {
                logger.warn("Business with ID {} not found", businessID);
                return new ResponseEntity<>("Business not found", HttpStatus.NOT_FOUND);
            }

            Config originalConfig = business.getConfig();
            if (originalConfig == null) {
                originalConfig = new Config();
            }

            if (changedConfig != null) {
                originalConfig.setEnableI18nInputSupport(changedConfig.getEnableI18nInputSupport());
                originalConfig.setEnableQRCode(changedConfig.getEnableQRCode());
                originalConfig.setEnableAppointment(changedConfig.getEnableAppointment());
                originalConfig.setEnableBillSumbission(changedConfig.getEnableBillSumbission());
            }

            business.setConfig(originalConfig);
            businessRepository.save(business);

            logger.info("Config updated successfully for business ID {}", businessID);
            return new ResponseEntity<>("Config updated successfully", HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error updating config for business ID " + businessID, e);
            return new ResponseEntity<>("An error occurred while updating the config", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
