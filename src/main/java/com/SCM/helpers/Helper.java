package com.SCM.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            
        
            
            String clientId = oauth2Token.getAuthorizedClientRegistrationId();
            String email = null;

            if ("google".equalsIgnoreCase(clientId)) {
                email = oauth2User.getAttribute("email");
            } else if ("github".equalsIgnoreCase(clientId)) {
                System.out.println("Getting email from GitHub");
                email = oauth2User.getAttribute("email") != null ? oauth2User.getAttribute("email").toString()
                        : oauth2User.getAttribute("login").toString() + "@github.com";
            } else if ("linkedin".equalsIgnoreCase(clientId)) {
                email = oauth2User.getAttribute("emailAddress"); 
            }

            return email != null ? email : "no-email-provided@" + clientId + ".com";
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        } else {
            return authentication.getName();
        }
    }
}
