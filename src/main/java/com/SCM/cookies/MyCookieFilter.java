package com.SCM.cookies;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.web.filter.GenericFilterBean;

public class MyCookieFilter  { // extends GenericFilterBean

//    public static final String MY_COOKIE_NAME = "your-cookie-name";
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
//        throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
//
//        checkCookie(httpServletRequest, httpServletResponse);
//
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    private void checkCookie(HttpServletRequest request, HttpServletResponse response) {
//        if (request.getCookies() != null) {
//            boolean cookieExists = Arrays.stream(request.getCookies())
//                .anyMatch(cookie -> cookie.getName().equalsIgnoreCase(MY_COOKIE_NAME));
//            if (!cookieExists) {
//                String cookieValue = generateSecureCookieValue();
//                Cookie newCookie = new Cookie(MY_COOKIE_NAME, cookieValue);
//                newCookie.setPath("/");  // Set path for the cookie if necessary
//                newCookie.setHttpOnly(true);  // Set HttpOnly flag for better security
//                newCookie.setSecure(true);  // Set Secure flag if using HTTPS
//                response.addCookie(newCookie);
//            }
//        }
//    }
//
//    private String generateSecureCookieValue() {
//        // Use a UUID for a simple unique identifier
//        UUID uuid = UUID.randomUUID();
//        return uuid.toString();
//    }
}
