package com.SCM.config;

import java.io.IOException;
import java.security.Key;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.SCM.dtoForJWT.JWTAuthFilter;
import com.SCM.dtoForJWT.JWTService;
import com.SCM.entities.Customer;
import com.SCM.entities.Providers;
import com.SCM.entities.User;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role;
import com.SCM.role.RoleRepository;
import com.SCM.service.SecuritycCustomerUserDetailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class OAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(OAuthenticationSuccessHandler.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JWTService jwtUtil;

    @Autowired
    SecuritycCustomerUserDetailService userDetailsService;
    
    @Autowired
    CustomerRepository customerRepository;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  /*  @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthenticationSuccessHandler");
        

        try {
            OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
            logger.info(authorizedClientRegistrationId);

            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
            oauthUser.getAttributes().forEach((key, value) -> logger.info("{} => {}", key, value));

            Providers provider = determineProvider(authorizedClientRegistrationId);

            String fullName = getAttributeValue(oauthUser, "name", "");
            String[] nameParts = splitFullName(fullName);
            String firstName = nameParts[0];
            String lastName = nameParts[1];

            String email = getAttributeValue(oauthUser, "email", null);
            if (email == null && "github".equalsIgnoreCase(authorizedClientRegistrationId)) {
                email = getAttributeValue(oauthUser, "login", "") + "@github.com";
            }

            saveCustomerIfNotExists(email, firstName, lastName);
            
            User finalUser = getUser(email, oauthUser, firstName, lastName, provider);

            UserDetails userDetails = userDetailsService.loadUserByUsername(finalUser.getEmail());
            logger.info("User details loaded successfully for user: {}", userDetails.getUsername());
            String token = jwtUtil.generateToken(userDetails);

            if (token != null) {
                logger.info("JWT token generated successfully for user: {}", token);
            } else {
                logger.error("Failed to generate JWT token for user: {}", userDetails.getUsername());
                throw new RuntimeException("Failed to generate JWT token for user");
            }

            setCookie(response, "token", token, Integer.MAX_VALUE, true);

           // response.sendRedirect("/user/dashboard");
            
            response.sendRedirect("/my_app/access");


        } catch (Exception e) {
            logger.error("Error during authentication success handling: ", e);
            response.sendRedirect("/error");
        }
    }

    private void saveCustomerIfNotExists(String email, String firstName, String lastName) {
    	 Optional<Customer> existingCustomerOptional = customerRepository.findByEmail(email);
         if (!existingCustomerOptional.isPresent()) {
             Customer newCustomer = new Customer();
             newCustomer.setEmail(email);
             newCustomer.setFirstName(firstName);
             newCustomer.setLastName(lastName);
             // Set other customer details if needed
             customerRepository.save(newCustomer);
         }
		
	}

	private Providers determineProvider(String authorizedClientRegistrationId) {
        if ("google".equalsIgnoreCase(authorizedClientRegistrationId)) {
            return Providers.GOOGLE;
        } else if ("github".equalsIgnoreCase(authorizedClientRegistrationId)) {
            return Providers.GITHUB;
        } else {
            // Handle other providers if needed
            return null;
        }
    }

    private String getAttributeValue(DefaultOAuth2User oauthUser, String attributeName, String defaultValue) {
        Object attributeValue = oauthUser.getAttribute(attributeName);
        return attributeValue != null ? attributeValue.toString() : defaultValue;
    }

    private String[] splitFullName(String fullName) {
        if (!fullName.isEmpty() && fullName.contains(" ")) {
            return fullName.split(" ", 2);
        } else {
            return new String[]{fullName, ""};
        }
    }

    private User getUser(String email, DefaultOAuth2User oauthUser, String firstName, String lastName, Providers provider) {
        Optional<User> existingUserOptional = email != null
                ? userRepository.findByEmail(email)
                : userRepository.findByProviderUserId(oauthUser.getName());

        if (existingUserOptional.isPresent()) {
            return existingUserOptional.get();
        } else {
            return createUser(email, oauthUser, firstName, lastName, provider);
        }
    }

    private User createUser(String email, DefaultOAuth2User oauthUser, String firstName, String lastName, Providers provider) {
        User newUser = new User();
        newUser.setEmail(email != null ? email : "no-email-provided@" + provider + ".com");
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPassword("password");
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setProvider(provider);
        newUser.setEnabled(true);
        newUser.setEmailVerified(true);
        newUser.setProviderUserId(oauthUser.getName());

        try {
            Role userRole = roleRepository.findByName(Role.RoleType.ROLE_CUSTOMER);
            if (userRole == null) {
                userRole = new Role();
                userRole.setName(Role.RoleType.ROLE_CUSTOMER);
                userRole = roleRepository.save(userRole);
            }
            newUser.setRole(userRole);
            return userRepository.save(newUser);
        } catch (Exception e) {
            logger.error("Error creating new user: ", e);
            throw new RuntimeException("Error creating new user", e);
        }
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setPath("/"); // Ensure the path is set to root added new
        response.addCookie(cookie);
        
    }*/
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthenticationSuccessHandler");

        try {
            OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
            logger.info(authorizedClientRegistrationId);

            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
            oauthUser.getAttributes().forEach((key, value) -> logger.info("{} => {}", key, value));

            Providers provider = determineProvider(authorizedClientRegistrationId);

            String fullName = getAttributeValue(oauthUser, "name", "");
            String[] nameParts = splitFullName(fullName);
            String firstName = nameParts[0];
            String lastName = nameParts[1];

            String email = getAttributeValue(oauthUser, "email", null);
            if (email == null && "github".equalsIgnoreCase(authorizedClientRegistrationId)) {
                email = getAttributeValue(oauthUser, "login", "") + "@github.com";
            }

            saveCustomerIfNotExists(email, firstName, lastName);
            
            User finalUser = getUser(email, oauthUser, firstName, lastName, provider);

            UserDetails userDetails = userDetailsService.loadUserByUsername(finalUser.getEmail());
            logger.info("User details loaded successfully for user: {}", userDetails.getUsername());
            String token = jwtUtil.generateToken(userDetails);

            if (token != null) {
                logger.info("JWT token generated successfully for user: {}", token);
            } else {
                logger.error("Failed to generate JWT token for user: {}", userDetails.getUsername());
                throw new RuntimeException("Failed to generate JWT token for user");
            }

            setCookie(response, "token", token, Integer.MAX_VALUE, true);

            response.sendRedirect("/my_app/access");

        } catch (Exception e) {
            logger.error("Error during authentication success handling: ", e);
            response.sendRedirect("/error");
        }
    }

    private void saveCustomerIfNotExists(String email, String firstName, String lastName) {
        Optional<Customer> existingCustomerOptional = customerRepository.findByEmail(email);
        if (!existingCustomerOptional.isPresent()) {
            Customer newCustomer = new Customer();
            newCustomer.setEmail(email);
            newCustomer.setFirstName(firstName);
            newCustomer.setLastName(lastName);
            // Set other customer details if needed
            customerRepository.save(newCustomer);
        }
    }

    private Providers determineProvider(String authorizedClientRegistrationId) {
        if ("google".equalsIgnoreCase(authorizedClientRegistrationId)) {
            return Providers.GOOGLE;
        } else if ("github".equalsIgnoreCase(authorizedClientRegistrationId)) {
            return Providers.GITHUB;
        } else {
            // Handle other providers if needed
            return null;
        }
    }

    private String getAttributeValue(DefaultOAuth2User oauthUser, String attributeName, String defaultValue) {
        Object attributeValue = oauthUser.getAttribute(attributeName);
        return attributeValue != null ? attributeValue.toString() : defaultValue;
    }

    private String[] splitFullName(String fullName) {
        if (!fullName.isEmpty() && fullName.contains(" ")) {
            return fullName.split(" ", 2);
        } else {
            return new String[]{fullName, ""};
        }
    }

    private User getUser(String email, DefaultOAuth2User oauthUser, String firstName, String lastName, Providers provider) {
        Optional<User> existingUserOptional = email != null
                ? userRepository.findByEmail(email)
                : userRepository.findByProviderUserId(oauthUser.getName());

        if (existingUserOptional.isPresent()) {
            return existingUserOptional.get();
        } else {
            return createUser(email, oauthUser, firstName, lastName, provider);
        }
    }

    private User createUser(String email, DefaultOAuth2User oauthUser, String firstName, String lastName, Providers provider) {
        User newUser = new User();
        newUser.setEmail(email != null ? email : "no-email-provided@" + provider + ".com");
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPassword("password");
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setProvider(provider);
        newUser.setEnabled(true);
        newUser.setEmailVerified(true);
        newUser.setProviderUserId(oauthUser.getName());

        try {
            Role userRole = roleRepository.findByName(Role.RoleType.ROLE_CUSTOMER);
            if (userRole == null) {
                userRole = new Role();
                userRole.setName(Role.RoleType.ROLE_CUSTOMER);
                userRole = roleRepository.save(userRole);
            }
            newUser.setRole(userRole);
            return userRepository.save(newUser);
        } catch (Exception e) {
            logger.error("Error creating new user: ", e);
            throw new RuntimeException("Error creating new user", e);
        }
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setPath("/"); // Ensure the path is set to root
        response.addCookie(cookie);
    }

}