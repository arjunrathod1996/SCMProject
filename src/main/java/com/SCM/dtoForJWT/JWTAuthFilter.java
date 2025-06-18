package com.SCM.dtoForJWT;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.SCM.service.SecuritycCustomerUserDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component
//public class JWTAuthFilter extends OncePerRequestFilter {
//
//    private final JWTService jwtService;
//    private final SecuritycCustomerUserDetailService customUserDetailsService;
//
//    @Autowired
//    public JWTAuthFilter(JWTService jwtService, SecuritycCustomerUserDetailService customUserDetailsService) {
//        this.jwtService = jwtService;
//        this.customUserDetailsService = customUserDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            String jwtToken = extractTokenFromRequest(request);
//
//            if (jwtToken != null) {
//                String username = null;
//                try {
//                    username = jwtService.extractUsername(jwtToken);
//                    System.out.println("username: " + username);
//                } catch (Exception e) {
//                    System.err.println("Error extracting username from JWT: " + e.getMessage());
//                }
//
//                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//
//                    System.out.println("name: " + userDetails.getUsername());
//                    System.out.println("password: " + userDetails.getPassword());
//
//                    if (jwtService.validateToken(jwtToken, userDetails)) {
//                        UsernamePasswordAuthenticationToken authenticationToken =
//                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                    } else {
//                        System.out.println("Token is not valid");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Error processing JWT token: " + e.getMessage());
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String extractTokenFromRequest(HttpServletRequest request) {
//        String token = null;
//        if (request.getCookies() != null) {
//            Cookie[] cookies = request.getCookies();
//            for (Cookie cookie : cookies) {
//                if ("token".equals(cookie.getName())) {
//                    token = cookie.getValue();
//                    break;
//                }
//            }
//        }
//        return token;
//    }  
//}


//@Component
//public class JWTAuthFilter extends OncePerRequestFilter {
//
//    private final JWTService jwtService;
//    private final SecuritycCustomerUserDetailService customUserDetailsService;
//
//    @Autowired
//    public JWTAuthFilter(JWTService jwtService, SecuritycCustomerUserDetailService customUserDetailsService) {
//        this.jwtService = jwtService;
//        this.customUserDetailsService = customUserDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            String jwtToken = extractTokenFromRequest(request);
//
//            if (jwtToken != null) {
//                String username = null;
//                try {
//                    username = jwtService.extractUsername(jwtToken);
//                    System.out.println("username >>>>>>>>>>>>>>>>>>>>>>>>>>>>> : " + username);
//                } catch (Exception e) {
//                    System.err.println("Error extracting username from JWT: " + e.getMessage());
//                }
//
//                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//
//                    System.out.println("name: " + userDetails.getUsername());
//                    System.out.println("password: " + userDetails.getPassword());
//
//                    if (jwtService.validateToken(jwtToken, userDetails)) {
//                        UsernamePasswordAuthenticationToken authenticationToken =
//                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                    } else {
//                        System.out.println("Token is not valid");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Error processing JWT token: " + e.getMessage());
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String extractTokenFromRequest(HttpServletRequest request) {
//        String token = null;
//        if (request.getCookies() != null) {
//            Cookie[] cookies = request.getCookies();
//            for (Cookie cookie : cookies) {
//                if ("token".equals(cookie.getName())) {
//                    token = cookie.getValue();
//                    break;
//                }
//            }
//        }
//        return token;
//    }  


@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthFilter.class);

    private final JWTService jwtService;
    private final SecuritycCustomerUserDetailService customUserDetailsService;

    @Autowired
    public JWTAuthFilter(JWTService jwtService, SecuritycCustomerUserDetailService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

  /*  @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwtToken = extractTokenFromRequest(request);

            if (jwtToken != null) {
                String username = null;
                try {
                    username = jwtService.extractUsername(jwtToken);
                    logger.info("Username extracted from JWT: {}", username);
                } catch (Exception e) {
                    logger.error("Error extracting username from JWT: {}", e.getMessage());
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    logger.info("Username: {}", userDetails.getUsername());
                    logger.info("Password: {}", userDetails.getPassword());

                    if (jwtService.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        logger.warn("Token is not valid");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }*/
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwtToken = extractTokenFromRequest(request);

            if (jwtToken != null) {
                String username = null;
                try {
                    username = jwtService.extractUsername(jwtToken);
                    logger.info("Username extracted from JWT: {}", username);
                } catch (Exception e) {
                    logger.error("Error extracting username from JWT: {}", e.getMessage());
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails;
                    
                    // Check if username is an email or phone number
                    if (isEmail(username)) {
                        userDetails = customUserDetailsService.loadUserByUsername(username);
                    } else {
                        userDetails = customUserDetailsService.loadUserByMobileAndOtp(username); // OTP validation is already done at this point
                    }

                    logger.info("Username: {}", userDetails.getUsername());
                    logger.info("Password: {}", userDetails.getPassword());

                    if (jwtService.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        logger.warn("Token is not valid");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
    
    private boolean isEmail(String username) {
        // Simple regex to check if the username is an email
        return username.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }
}



