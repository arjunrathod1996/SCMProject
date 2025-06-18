package com.SCM.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SCM.cartItem.BillingSummary;
import com.SCM.cartItem.BillingSummaryRepository;
import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.helpers.ResourceNotFoundException;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role.RoleType;
import com.SCM.role.RoleRepository;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private BillingSummaryRepository billingSummaryRepository;
    
//    @Autowired
//    RoleRepository roleRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public User saveUser(User user) {
        logger.debug("user Saved");
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
     // Fetch or create the ROLE_USER role
//        Role userRole = roleRepository.findByName(Role.RoleType.ROLE_CUSTOMER)
//                .orElseGet(() -> {
//                    Role newRole = new Role();
//                    newRole.setName(Role.RoleType.ROLE_CUSTOMER);
//                    return roleRepository.save(newRole);
//                });
//
//        user.setRole(userRole);

       // user.setRoleList(List.of(AppConstants.ROLE_USER));

        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // @Override
    // public Optional<User> updateUserById(User user) {
    //     return userRepository.findById(user.getId()).orElseThrow(()=> new ResourceNotFoundException("User Not Found"));
    // }

    @Override
    public Optional<User> updateUserById(User user) {
        return userRepository.findById(user.getId()).map(existingUser -> {
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setEmailVerified(user.isEmailVerified());
            existingUser.setProfilePic(user.getProfilePic());
            existingUser.setPhoneVerfied(user.isPhoneVerfied());
            user.setProviderUserId(user.getProviderUserId());
            //existingUser.setCompany(user.getCompany());
            return userRepository.save(existingUser);
        });
    }


    @Override
    public boolean isUserExist(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isUserExist'");
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser != null ? true : false;
    }

    @Override
    public List<User> getAllUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllUser'");
    }

    @Override
    public void deleteUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
        } else {
            throw new ResourceNotFoundException("User id Not Found");
        }
    }

    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    
    public List<BillingSummary> getBillingSummaryByUser(User user) {
        try {
            if (user.getRole().getName().equals(RoleType.ROLE_MERCHANT_ADMIN)) {
                // Fetch all billing summaries for merchants under the same business
                List<Merchant> merchants = merchantRepository.findByBusiness(user.getBusiness());
                List<Long> merchantIds = merchants.stream().map(Merchant::getId).collect(Collectors.toList());
                return billingSummaryRepository.findByMerchantIds(merchantIds);
            } else if (user.getRole().getName().equals(RoleType.ROLE_ADMIN)) {
                // Fetch all billing summaries for merchants under the admin's business
                Business business = user.getMerchant().getBusiness();
                List<Merchant> merchants = merchantRepository.findByBusiness(business);
                List<Long> merchantIds = merchants.stream().map(Merchant::getId).collect(Collectors.toList());
                return billingSummaryRepository.findByMerchantIds(merchantIds);
            } else if (user.getRole().getName().equals(RoleType.ROLE_MERCHANT_STAFF)) {
                // Fetch all billing summaries for the specific merchant
                List<BillingSummary> billingSummaries = billingSummaryRepository.findByMerchantId(user.getMerchant().getId());
                // Filter billing summaries added by the staff user
                return billingSummaries.stream()
                        .filter(summary -> summary.getUser().equals(user.getEmail())) // Assuming email is used for createdBy
                        .collect(Collectors.toList());
            } else {
                throw new RuntimeException("Unsupported role: " + user.getRole().getName());
            }
        } catch (Exception e) {
            logger.error("Error fetching billing summaries for user: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching billing summaries for user: " + e.getMessage());
        }
    }


}
