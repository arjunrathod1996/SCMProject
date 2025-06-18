package com.SCM.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.SCM.controllers.Country;
import com.SCM.controllers.Region;
import com.SCM.entities.Business;
import com.SCM.repository.BusinessRepository;
import com.SCM.repository.CountryRepository;
import com.SCM.repository.RegionRepository;

import jakarta.transaction.Transactional;

@Service
public class LocationService {
	
	 @Autowired
	 CountryRepository countryRepository;
	 @Autowired
	 RegionRepository regionRepository;
	    
	 private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

	@Transactional
    public Country saveCountry(Country country) {
        Long businessId = country.getId();
        logger.info("Attempting to save or update country with ID: {}", businessId);
        if (businessId == null || !countryRepository.existsById(businessId)) {
            // Business doesn't exist in the repository, so save it
            logger.info("Saving a new country.");
            return countryRepository.save(country);
        } else {
            // Business already exists, update its fields and save
            logger.info("Updating an existing country with ID: {}", businessId);
            return updateExistingCountry(country);
        }
    }

    private Country updateExistingCountry(Country country) {
        Long countryId = country.getId();
        Country existingCountry = countryRepository.findById(countryId).orElse(null);

        if (existingCountry != null) {
            logger.info("Country found with ID {}. Updating its fields.", countryId);
            existingCountry.setName(country.getName());
            existingCountry.setCallingCode(country.getCallingCode());
            return countryRepository.save(existingCountry);
        } else {
            // Handle the case where business with the given ID doesn't exist
            logger.error("Country with ID {} not found for update.", countryId);
            // You can throw an exception or handle it according to your application logic
            return null;
        }
    }
    
    
    @Transactional
    public Region saveRegion(Region region,Long countryID) {
        Long regionId = region.getId();
        logger.info("Attempting to save or update region with ID: {}", regionId);
        if (regionId == null || !regionRepository.existsById(regionId)) {
        	Country country = countryRepository.findById(countryID).get();
        	region.setCountry(country);
            // Business doesn't exist in the repository, so save it
            logger.info("Saving a new region.");
            return regionRepository.save(region);
        } else {
            // Business already exists, update its fields and save
            logger.info("Updating an existing region with ID: {}", regionId);
            return updateExistingRegion(region);
        }
    }

    private Region updateExistingRegion(Region region) {
        Long regionId = region.getId();
        Region existingRegion = regionRepository.findById(regionId).orElse(null);

        if (existingRegion != null) {
            logger.info("Region found with ID {}. Updating its fields.", regionId);
            existingRegion.setCity(region.getCity());
            existingRegion.setState(region.getState());
            existingRegion.setZone(region.getZone());
           // existingRegion.setCountry(region.getCountry());
            return regionRepository.save(existingRegion);
        } else {
            // Handle the case where business with the given ID doesn't exist
            logger.error("Region with ID {} not found for update.", regionId);
            // You can throw an exception or handle it according to your application logic
            return null;
        }
    }

	 public Page<Country> getCountry(Pageable pageable) {
        logger.info("Fetching countries with pageable: {}", pageable);

        Sort sort = pageable.getSort();
        if (sort == null || !sort.isSorted()) {
            logger.debug("No sort criteria found. Applying default sort by 'creationTime' descending.");
            sort = Sort.by(Sort.Direction.DESC, "creationTime");
        }

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        logger.debug("Page request created with page number: {}, page size: {}, sort: {}",
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort());

        try {
            Page<Country> countryPage = countryRepository.findCountryPageWise(pageRequest);
            logger.info("Countries fetched successfully with total elements: {}", countryPage.getTotalElements());
            return countryPage;
        } catch (Exception e) {
            logger.error("Error fetching countries with pageable: {}", pageable, e);
            throw e; // Optionally, you can handle the exception in a way that suits your application
        }
    }

	 public Page<Region> getRegion(Pageable pageable) {
        logger.info("Fetching regions with pageable: {}", pageable);

        Sort sort = pageable.getSort();
        if (sort == null || !sort.isSorted()) {
            logger.debug("No sort criteria found. Applying default sort by 'creationTime' descending.");
            sort = Sort.by(Sort.Direction.DESC, "creationTime");
        }

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        logger.debug("Page request created with page number: {}, page size: {}, sort: {}",
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort());

        try {
            Page<Region> regionPage = regionRepository.findRegionPageWise(pageRequest);
            logger.info("regions fetched successfully with total elements: {}", regionPage.getTotalElements());
            return regionPage;
        } catch (Exception e) {
            logger.error("Error fetching regions with pageable: {}", pageable, e);
            throw e; // Optionally, you can handle the exception in a way that suits your application
        }
    }

	
	
	
}
