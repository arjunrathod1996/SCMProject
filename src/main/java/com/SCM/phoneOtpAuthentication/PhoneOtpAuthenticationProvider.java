package com.SCM.phoneOtpAuthentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.SCM.entities.User;
import com.SCM.repository.UserRepository;

@Component
public class PhoneOtpAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phoneNumber = (String) authentication.getPrincipal();
        String otp = (String) authentication.getCredentials();

        User user = userRepository.findByPhoneNumber(phoneNumber);

        if (user != null && user.getOtp().equals(otp)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
            return new PhoneOtpAuthenticationToken(userDetails, otp, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Invalid OTP or phone number");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PhoneOtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}