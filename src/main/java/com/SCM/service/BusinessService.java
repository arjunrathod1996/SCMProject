package com.SCM.service;

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
import com.SCM.repository.BusinessRepository;

import jakarta.transaction.Transactional;

@Service
public class BusinessService {

    @Autowired
    BusinessRepository businessRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessService.class);
    
    
    @Transactional
    public Business saveBusiness(Business business) {
        Long businessId = business.getId();
        logger.info("Attempting to save or update business with ID: {}", businessId);

        if (businessId == null || !businessRepository.existsById(businessId)) {
            // Business doesn't exist in the repository, so save it
            logger.info("Saving a new business.");
            return businessRepository.save(business);
        } else {
            // Business already exists, update its fields and save
            logger.info("Updating an existing business with ID: {}", businessId);
            return updateExistingBusiness(business);
        }
    }

    public Business updateExistingBusiness(Business business) {
        Long businessId = business.getId();
        Business existingBusiness = businessRepository.findById(businessId).orElse(null);

        if (existingBusiness != null) {
            logger.info("Business found with ID {}. Updating its fields.", businessId);
            existingBusiness.setName(business.getName());
            existingBusiness.setFullName(business.getFullName());
            existingBusiness.setAddress(business.getAddress());
            existingBusiness.setDescription(business.getDescription());
            existingBusiness.setCategory(business.getCategory());
            return businessRepository.save(existingBusiness);
        } else {
            // Handle the case where business with the given ID doesn't exist
            logger.error("Business with ID {} not found for update.", businessId);
            // You can throw an exception or handle it according to your application logic
            return null;
        }
    }

//    public Business saveBusiness(Business business) {
//        if (business.getId() == null || !businessRepository.existsById(business.getId())) {
//            // Business doesn't exist in the repository, so save it
//            return businessRepository.save(business);
//        } else {
//            // Business already exists, update its fields and save
//            Business existingBusiness = businessRepository.findById(business.getId()).orElse(null);
//            if (existingBusiness != null) {
//                existingBusiness.setName(business.getName());
//                existingBusiness.setFullName(business.getFullName());
//                existingBusiness.setAddress(business.getAddress());
//                existingBusiness.setDescription(business.getDescription());
//                return businessRepository.save(existingBusiness);
//            } else {
//                // Handle the case where business with the given ID doesn't exist
//                // You can throw an exception or handle it according to your application logic
//                return null;
//            }
//        }
//    }

    public Page<Business> getBusiness(Pageable pageable) {
        logger.info("Fetching businesses with pageable: {}", pageable);

        Sort sort = pageable.getSort();
        if (sort == null || !sort.isSorted()) {
            logger.debug("No sort criteria found. Applying default sort by 'creationTime' descending.");
            sort = Sort.by(Sort.Direction.DESC, "creationTime");
        }

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        logger.debug("Page request created with page number: {}, page size: {}, sort: {}",
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort());

        try {
            Page<Business> businessPage = businessRepository.findBusinessPageWisee(pageRequest);
            logger.info("Businesses fetched successfully with total elements: {}", businessPage.getTotalElements());
            return businessPage;
        } catch (Exception e) {
            logger.error("Error fetching businesses with pageable: {}", pageable, e);
            throw e;
        }
    }
    
    
    public Optional<Business> findByUserId(Long userId) {
        logger.info("Fetching business with userId: {}", userId);

        try {
            Optional<Business> business = businessRepository.findById(userId);
            if (business.isPresent()) {
                logger.info("Business found with userId: {}", userId);
            } else {
                logger.warn("No business found with userId: {}", userId);
            }
            return business;
        } catch (Exception e) {
            logger.error("Error fetching business with userId: {}", userId, e);
            throw e;
        }
    }

    public void deleteBusiness(Long id) {
        logger.info("Deleting business with id: {}", id);

        try {
            Optional<Business> optionalBusiness = businessRepository.findById(id);
            if (optionalBusiness.isPresent()) {
                Business business = optionalBusiness.get();
                business.setDeleted(true);
                businessRepository.save(business);
                logger.info("Business marked as deleted with id: {}", id);
            } else {
                logger.warn("No business found with id: {}", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting business with id: {}", id, e);
            throw e;
        }
    }
}
