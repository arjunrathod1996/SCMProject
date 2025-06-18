package com.SCM.cookies;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private InMemoryTokenRepositoryImpl tokenRepository;

    public void authenticateUser() {
        // Perform authentication logic...

        // Generate a unique remember-me token series
        String series = UUID.randomUUID().toString();

        // Generate a remember-me token value
        String tokenValue = UUID.randomUUID().toString();

        // Store the remember-me token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        tokenRepository.createNewToken(new PersistentRememberMeToken(authentication.getName(), series, tokenValue, new java.util.Date()));
    }
}
