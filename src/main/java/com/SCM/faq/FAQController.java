package com.SCM.faq;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.controllers.BusinessController;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.service.CommonService;

@RestController
@RequestMapping("/api")
public class FAQController {
	
	@Autowired
	FAQService faqService;
	@Autowired
	CommonService commonService;
	@Autowired
	FAQListRepository faqListRepository;

	private static final Logger logger = LoggerFactory.getLogger(FAQController.class);
	
	@PostMapping("/faq")
	public ResponseEntity<FAQ> saveFAQ(@RequestBody FAQ faq, @RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "faqList", required = true) Long faqListId) {
		User user = commonService.getLoggedInUser();
	
		logger.info("Received request to save faq with ID: {}", id);
		
		if (faq == null) {
			logger.error("Invalid input received. Request body is null.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		FAQList faqList = faqListRepository.findById(faqListId).get();
		faq.setFaqList(faqList);

		if (id != null) {
			faq.setId(id);
			logger.info("Received business ID as a parameter: {}", id);
		}

		FAQ savedFAQ = faqService.saveFaq(faq, user.getBusiness());
		if (savedFAQ != null) {
			logger.info("FAQ saved successfully with ID: {}", savedFAQ.getId());
			return new ResponseEntity<>(savedFAQ, HttpStatus.CREATED);
		} else {
			logger.error("Failed to save FAQ.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	 
	 
	 @PostMapping("/faqList")
	    public ResponseEntity<FAQList> saveFAQList(@RequestBody FAQList faq, @RequestParam(value = "id", required = false) Long id) {
		 	User user = commonService.getLoggedInUser();
	        logger.info("Received request to save faqlist with ID: {}", id);
	        if (faq == null) {
	            logger.error("Invalid input received. Request body is null.");
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	        }
	
	        if (id != null) {
	            faq.setId(id);
	            logger.info("Received business ID as a parameter: {}", id);
	        }
	
	        FAQList savedFAQ = faqService.saveFaqList(faq , user.getBusiness());
	        if (savedFAQ != null) {
	            logger.info("FAQ saved successfully with ID: {}", savedFAQ.getId());
	            return new ResponseEntity<>(savedFAQ, HttpStatus.CREATED);
	        } else {
	            logger.error("Failed to saveFAQ.");
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	 
	 	@RequestMapping(value = "/faqListPageWise", method = RequestMethod.GET)
	    @ResponseBody
	    public Page<?> getFAQListPageWise(Pageable pageable,
	                                           @RequestParam(value = "address", required = false) String address,
	                                           @RequestParam(value = "name", required = false) String name,
	                                           @RequestParam(value = "startDate", required = false) String startDate,
	                                           @RequestParam(value = "endDate", required = false) String endDate) {
	        logger.info("Received request to fetch faq list page-wise.");
	        logger.debug("Received parameters: name={}, address={}, startDate={}, endDate={}", name, address, startDate, endDate);
	        return faqService.getFAQList(pageable);
	    }
	 	
	 	@RequestMapping(value = "/faqPageWise", method = RequestMethod.GET)
	    @ResponseBody
	    public Page<?> getFAQPageWise(Pageable pageable,
	                                           @RequestParam(value = "address", required = false) String address,
	                                           @RequestParam(value = "name", required = false) String name,
	                                           @RequestParam(value = "startDate", required = false) String startDate,
	                                           @RequestParam(value = "endDate", required = false) String endDate) {
	        logger.info("Received request to fetch faq page-wise.");
	        logger.debug("Received parameters: name={}, address={}, startDate={}, endDate={}", name, address, startDate, endDate);
	        return faqService.getFAQ(pageable);	    }
	 	
	 	
	 	 @RequestMapping(value = "/faqList", method = RequestMethod.GET)
	     public ResponseEntity<?> getFAQListById(@RequestParam Long id) {
	         logger.info("Received request to fetch faq list with ID: {}", id);
	         try {
	             Optional<FAQList> faq = faqService.findByFaqListId(id);
	             if (faq.isPresent()) {
	                 logger.info("FAQList found with ID: {}", id);
	                 return ResponseEntity.ok(faq.get());
	             } else {
	                 logger.warn("No faq lsit found with ID: {}", id);
	                 return ResponseEntity.notFound().build();
	             }
	         } catch (Exception e) {
	             logger.error("Error fetching faq list data: {}", e.getMessage());
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching business data");
	         }
	     }
	 	 
	 	 @RequestMapping(value = "/faq", method = RequestMethod.GET)
	     public ResponseEntity<?> getFAQId(@RequestParam Long id) {
	         logger.info("Received request to fetch faq list with ID: {}", id);
	         try {
	             Optional<FAQ> faq = faqService.findByFaqId(id);
	             if (faq.isPresent()) {
	                 logger.info("FAQList found with ID: {}", id);
	                 return ResponseEntity.ok(faq.get());
	             } else {
	                 logger.warn("No faq lsit found with ID: {}", id);
	                 return ResponseEntity.notFound().build();
	             }
	         } catch (Exception e) {
	             logger.error("Error fetching faq list data: {}", e.getMessage());
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching business data");
	         }
	     }

}
