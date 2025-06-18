package com.SCM.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.SCM.entities.CustomUserDetails;
import com.SCM.entities.User;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

@Service
public class SecuritycCustomerUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

//  @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> userOptional = userRepository.findByEmail(username);   
//        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        
//        return new CustomUserDetails(user);  
//    }
  
  
//  @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  
//        Optional<User> userOptional = userRepository.findByEmail(username);   
//        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        // Get the single role assigned to the user
//        Role role = user.getRole();
//        // Create a SimpleGrantedAuthority from the role
//        GrantedAuthority authority = new SimpleGrantedAuthority(role.getName().name());       
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getEmail())
//                .password(user.getPassword())
//                .authorities(Collections.singletonList(authority))  
//                .build();
//    }    
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);

        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return buildUserDetails(user);
    }

    public UserDetails loadUserByMobileAndOtp(String mobileNumber) {
        User user = userRepository.findByPhoneNumber(mobileNumber);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with mobile number: " + mobileNumber);
        }

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        Role role = user.getRole();
        GrantedAuthority authority = new SimpleGrantedAuthority(role.getName().name());

        // Use the email if it exists, otherwise use the phone number
        String username = (user.getEmail() != null && !user.getEmail().isEmpty()) ? user.getEmail() : user.getPhoneNumber();

        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getPassword() != null ? user.getPassword() : "") // Password can be empty for OTP auth
                .authorities(Collections.singletonList(authority))
                .build();
    }
}
