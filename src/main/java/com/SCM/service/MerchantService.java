package com.SCM.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SCM.controllers.Country;
import com.SCM.controllers.Merchant;
import com.SCM.controllers.MerchantController;
import com.SCM.controllers.Region;
import com.SCM.dao.MerchantUserDTO;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.helpers.EmailAlreadyExistsException;
import com.SCM.repository.BusinessRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.RegionRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role;
import com.SCM.role.Role.RoleType;
import com.SCM.role.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class MerchantService {
	
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	RegionRepository regionRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
	RoleRepository roleRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(MerchantController.class);

	public Page<Merchant> getMerchant(Pageable pageable, User user) {
	    logger.info("Fetching merchants with pageable: {}", pageable);

	    Sort sort = pageable.getSort();
	    if (sort == null || !sort.isSorted()) {
	        logger.debug("No sort criteria found. Applying default sort by 'creationTime' descending.");
	        sort = Sort.by(Sort.Direction.DESC, "creationTime");
	    }

	    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
	    logger.debug("Page request created with page number: {}, page size: {}, sort: {}",
	            pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort());

	    // Determine the business for the user based on role
	    Business business = null;
	    Merchant merchant = null;
	    if (user.getRole().getName() == Role.RoleType.ROLE_MERCHANT_ADMIN) {
	        business = user.getBusiness();
	    }
	   
	    
	    try {
	        Page<Merchant> merchantPage;
	        if (business != null) {
	            // Fetch related merchants for merchant admin
	            merchantPage = merchantRepository.findMerchantPageWisee(pageRequest, business.getId());
	        } else {
	            // Fetch all merchants for admin
	            merchantPage = merchantRepository.findMerchantPageWisee(pageRequest, null);
	        }

	        logger.info("Merchants fetched successfully with total elements: {}", merchantPage.getTotalElements());
	        return merchantPage;
	    } catch (Exception e) {
	        logger.error("Error fetching merchant with pageable: {}", pageable, e);
	        throw e;
	    }
	}

	
	
	public Page<User> getMerchantUser(User user,Pageable pageable) {
        logger.info("Fetching merchants user with pageable: {}", pageable);

        Sort sort = pageable.getSort();
        if (sort == null || !sort.isSorted()) {
            logger.debug("No sort criteria found. Applying default sort by 'creationTime' descending.");
            sort = Sort.by(Sort.Direction.DESC, "creationTime");
        }

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        logger.debug("Page request created with page number: {}, page size: {}, sort: {}",
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort());

        try {
            Page<User> merchantUserPage = userRepository.findMerchantUserPageWisee(pageRequest);
            logger.info("Merchants User fetched successfully with total elements: {}", merchantUserPage.getTotalElements());
            return merchantUserPage;
        } catch (Exception e) {
            logger.error("Error fetching merchant user with pageable: {}", pageable, e);
            throw e;
        }
    }

	@Transactional
	public Merchant saveMerchant(Merchant merchant, Long businessID, Long regionID) {
		 Long merchantId = merchant.getId();
	        logger.info("Attempting to save or update merchant with ID: {}", merchantId);
	        if (merchantId == null || !merchantRepository.existsById(merchantId)) {
	        	Business business = businessRepository.findById(businessID).get();
	        	Region region = regionRepository.findById(regionID).get();
	        	merchant.setBusiness(business);
	        	merchant.setRegion(region);
	            // Business doesn't exist in the repository, so save it
	            logger.info("Saving a new region.");
	            return merchantRepository.save(merchant);
	        } else {
	            // Business already exists, update its fields and save
	            logger.info("Updating an existing merchant with ID: {}", merchantId);
	            return updateExistingMerchant(merchant);
	        }
	}

	private Merchant updateExistingMerchant(Merchant merchant) {
		Long merchantId = merchant.getId();
        Merchant existingMerchant = merchantRepository.findById(merchantId).orElse(null);

        if (existingMerchant != null) {
            logger.info("Merchant found with ID {}. Updating its fields.", merchantId);
            existingMerchant.setName(merchant.getName());
            existingMerchant.setShortLink(merchant.getShortLink());
            existingMerchant.setMobileNumber(merchant.getMobileNumber());
            existingMerchant.setLocality(merchant.getLocality());
            existingMerchant.setAddress(merchant.getAddress());
            return merchantRepository.save(existingMerchant);
        } else {
            // Handle the case where business with the given ID doesn't exist
            logger.error("Merchant with ID {} not found for update.", merchantId);
            // You can throw an exception or handle it according to your application logic
            return null;
        }
	}


	/*public User saveMerchantUser(User user, Long businessID, Long merchantID) {
		 Long userId = user.getId();
	        logger.info("Attempting to save or update merchant user with ID: {}", userId);
	        if (userId == null || !merchantRepository.existsById(userId)) {
	        	Business business = businessRepository.findById(businessID).get();
	        	Merchant merchant = merchantRepository.findById(merchantID).get();
	        	user.setBusiness(business);
	        	user.setMerchant(merchant);
	            // User doesn't exist in the repository, so save it
	            logger.info("Saving a new user.");
	            return userRepository.save(user);
	        } else {
	            // Business already exists, update its fields and save
	            logger.info("Updating an existing merchant user with ID: {}", userId);
	            return updateExistingMerchantUser(user);
	        }
	}


	private User updateExistingMerchantUser(User user) {
		Long userId = user.getId();
        User existingMerchantUser = userRepository.findById(userId).orElse(null);

        if (existingMerchantUser != null) {
            logger.info("Merchant User found with ID {}. Updating its fields.", userId);
            existingMerchantUser.setRole(user.getRole());
            existingMerchantUser.setEmail(user.getEmail());
            existingMerchantUser.setPassword(user.getPassword());
            return userRepository.save(existingMerchantUser);
        } else {
            // Handle the case where business with the given ID doesn't exist
            logger.error("Merchant user with ID {} not found for update.", userId);
            // You can throw an exception or handle it according to your application logic
            return null;
        }
	}*/
	
	
	public User saveMerchantUser(User user, Long businessID, Long merchantID) {
        Long userId = user.getId();
        logger.info("Attempting to save or update merchant user with ID: {}", userId);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
        }

        if (userId == null || !merchantRepository.existsById(userId)) {
            Business business = businessRepository.findById(businessID).orElseThrow(() -> new EntityNotFoundException("Business not found"));
            Merchant merchant = merchantRepository.findById(merchantID).orElseThrow(() -> new EntityNotFoundException("Merchant not found"));
            Role role = roleRepository.findByRoleName(user.getRoleName());
            
            user.setRole(role);
            if(Role.RoleType.ROLE_MERCHANT_STAFF.name() == role.getName().name()) {
            	user.setMerchant(merchant);
            }
            if(Role.RoleType.ROLE_MERCHANT_ADMIN.name() == role.getName().name()) {
            	user.setBusiness(business);
            }
            // Encode the password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            logger.info("Saving a new user.");
            
            return userRepository.save(user);
        } else {
            logger.info("Updating an existing merchant user with ID: {}", userId);
            return updateExistingMerchantUser(user);
        }
    }

    private User updateExistingMerchantUser(User user) {
        Long userId = user.getId();
        User existingMerchantUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Merchant user not found"));
        Role role = roleRepository.findByRoleName(user.getRoleName());
        if (!existingMerchantUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
        }

        existingMerchantUser.setRole(user.getRole());
        existingMerchantUser.setEmail(user.getEmail());
        existingMerchantUser.setPassword(user.getPassword());
        if(Role.RoleType.ROLE_MERCHANT_STAFF.name() == role.getName().name()) {
        	existingMerchantUser.setMerchant(user.getMerchant());
        }
        // Encode the password before updating
        existingMerchantUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(existingMerchantUser);
    }


    public List<User> getMerchants(String shortLink) {
        Merchant me = merchantRepository.findByShortLink(shortLink);  
        logger.info("Merchant ID: {}", me.getId());
        List<User> merchantUser = userRepository.findByRelatedMerchants(me.getId());
        for (User m : merchantUser) {
            logger.info("User Email: {}, Role: {}", m.getEmail(), m.getRole().getName().name());
        }
        return merchantUser;
    }


	public List<Merchant> getMerchantss(String shortLink) {
		 List<Merchant> me = merchantRepository.findByShortLink_(shortLink); 
	        logger.info("Merchant ID: {}", me);
	       
	       
	        return me;
	}
	
	public List<MerchantUserDTO> getMerchantsWithLinks(String shortLink) {
        List<Object[]> results = merchantRepository.findMerchantAndUserLinks(shortLink);
        List<MerchantUserDTO> dtos = new ArrayList<>();
        for (Object[] result : results) {
            String merchantShortLink = (String) result[0];
            String userStaffLink = (String) result[1];
            dtos.add(new MerchantUserDTO(merchantShortLink, userStaffLink));
        }
        return dtos;
    }


	public Optional<Merchant> findByMerchantId(Long id) {
		logger.info("Fetching merchant with userId: {}", id);

        try {
            Optional<Merchant> merchant = merchantRepository.findById(id);
            if (merchant.isPresent()) {
                logger.info("Merchant found with userId: {}", id);
            } else {
                logger.warn("No merchant found with userId: {}", id);
            }
            return merchant;
        } catch (Exception e) {
            logger.error("Error fetching merchant with userId: {}", id, e);
            throw e;
        }
	}

}
