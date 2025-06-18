package com.SCM.cookies;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

//@Component
public class CacheControlFilter  {  // implements Filter
	
//	 @Override
//	    public void doFilter(ServletRequest request, ServletResponse response,
//	            FilterChain chain) throws IOException, ServletException {
//
//			HttpServletResponse resp = (HttpServletResponse) response;
//			resp.setHeader("Expires", "Tue, 03 Jul 2001 06:00:00 GMT");
//			resp.setHeader("Last-Modified", new Date().toString());
//			resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
//			resp.setHeader("Pragma", "no-cache");
//			
//			chain.doFilter(request, response);
//			}
//
//    
//    
//  
//    @Override
//    public void destroy() {
//        // Cleanup code goes here if needed
//    }
//    
//    @Override
//    public void init(FilterConfig arg0) throws ServletException {}
	
}
