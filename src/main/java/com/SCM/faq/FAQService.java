package com.SCM.faq;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.SCM.entities.Business;

import jakarta.transaction.Transactional;

@Service
public class FAQService {
	
	@Autowired
	FAQRepository faqRepository;
	@Autowired
	FAQListRepository faqListRepository;

	private static final Logger logger = LoggerFactory.getLogger(FAQService.class);
    
    
    @Transactional
    public FAQ saveFaq(FAQ faq,Business business) {
        Long faqId = faq.getId();
        logger.info("Attempting to save or update faq with ID: {}", faqId);
        if (faqId == null || !faqRepository.existsById(faqId)) {
            // FAQ doesn't exist in the repository, so save it
        	faq.setBusiness(business);
            logger.info("Saving a new FAQ.");
            return faqRepository.save(faq);
        } else {
            // Business already exists, update its fields and save
            logger.info("Updating an existing faq with ID: {}", faqId);
            return updateExistingFAQ(faq);
        }
    }

    private FAQ updateExistingFAQ(FAQ faq) {
        Long faqId = faq.getId();
        FAQ existingFAQ = faqRepository.findById(faqId).orElse(null);

        if (existingFAQ != null) {
            logger.info("FAQ found with ID {}. Updating its fields.", faqId);
            existingFAQ.setFaqList(faq.getFaqList());
            existingFAQ.setAnswer(faq.getAnswer());
            existingFAQ.setQuestion(faq.getQuestion());
            existingFAQ.setTopic(faq.getTopic());
            existingFAQ.setOrder(faq.getOrder());
            return faqRepository.save(existingFAQ);
        } else {
            // Handle the case where business with the given ID doesn't exist
            logger.error("FAQ with ID {} not found for update.", faqId);
            // You can throw an exception or handle it according to your application logic
            return null;
        }
    }
    
    
    @Transactional
    public FAQList saveFaqList(FAQList faq,Business business) {
        Long faqId = faq.getId();
        logger.info("Attempting to save or update faq with ID: {}", faqId);
        if (faqId == null || !faqListRepository.existsById(faqId)) {
            // FAQ doesn't exist in the repository, so save it
        	faq.setBusiness(business);
            logger.info("Saving a new FAQ.");
            return faqListRepository.save(faq);
        } else {
            // Business already exists, update its fields and save
            logger.info("Updating an existing faq with ID: {}", faqId);
            return updateExistingFAQList(faq);
        }
    }

    private FAQList updateExistingFAQList(FAQList faq) {
        Long faqId = faq.getId();
        FAQList existingFAQ = faqListRepository.findById(faqId).orElse(null);

        if (existingFAQ != null) {
            logger.info("FAQ found with ID {}. Updating its fields.", faqId);
            existingFAQ.setTopic(faq.getTopic());
            existingFAQ.setOrder(faq.getOrder());
            return faqListRepository.save(existingFAQ);
        } else {
            // Handle the case where business with the given ID doesn't exist
            logger.error("FAQ with ID {} not found for update.", faqId);
            // You can throw an exception or handle it according to your application logic
            return null;
        }
    }
    
    
    public Page<FAQ> getFAQ(Pageable pageable) {
        logger.info("Fetching faq with pageable: {}", pageable);

        Sort sort = pageable.getSort();
        if (sort == null || !sort.isSorted()) {
            logger.debug("No sort criteria found. Applying default sort by 'creationTime' descending.");
            sort = Sort.by(Sort.Direction.DESC, "creationTime");
        }

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        logger.debug("Page request created with page number: {}, page size: {}, sort: {}",
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort());

        try {
            Page<FAQ> faqPage = faqRepository.findFaqPageWise(pageRequest);
            logger.info("Faq fetched successfully with total elements: {}", faqPage.getTotalElements());
            return faqPage;
        } catch (Exception e) {
            logger.error("Error fetching faq with pageable: {}", pageable, e);
            throw e;
        }
    }
    
 
    public Optional<FAQ> findByFaqId(Long userId) {
        logger.info("Fetching business with userId: {}", userId);

        try {
            Optional<FAQ> faq =faqRepository.findById(userId);
            if (faq.isPresent()) {
                logger.info("Faq found with userId: {}", userId);
            } else {
                logger.warn("No Faq found with userId: {}", userId);
            }
            return faq;
        } catch (Exception e) {
            logger.error("Error fetching faq with userId: {}", userId, e);
            throw e;
        }
    }
    
    
    public Page<FAQList> getFAQList(Pageable pageable) {
        logger.info("Fetching faqList with pageable: {}", pageable);

        Sort sort = pageable.getSort();
        if (sort == null || !sort.isSorted()) {
            logger.debug("No sort criteria found. Applying default sort by 'creationTime' descending.");
            sort = Sort.by(Sort.Direction.DESC, "creationTime");
        }

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        logger.debug("Page request created with page number: {}, page size: {}, sort: {}",
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort());

        try {
            Page<FAQList> faqPage = faqListRepository.findFaqListPageWise(pageRequest);
            logger.info("FaqList fetched successfully with total elements: {}", faqPage.getTotalElements());
            return faqPage;
        } catch (Exception e) {
            logger.error("Error fetching faqList with pageable: {}", pageable, e);
            throw e;
        }
    }
    
 
    public Optional<FAQList> findByFaqListId(Long userId) {
        logger.info("Fetching business with userId: {}", userId);

        try {
            Optional<FAQList> faq =faqListRepository.findById(userId);
            if (faq.isPresent()) {
                logger.info("FaqList found with userId: {}", userId);
            } else {
                logger.warn("No FaqList found with userId: {}", userId);
            }
            return faq;
        } catch (Exception e) {
            logger.error("Error fetching faqList with userId: {}", userId, e);
            throw e;
        }
    }
	
	
}
