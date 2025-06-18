package com.SCM.dtoForJWT;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.SCM.helpers.ExceptionMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint
	{
//		@Override
//		public void commence(HttpServletRequest request, HttpServletResponse response,
//				AuthenticationException authException) throws IOException, ServletException {	
//			ExceptionMessage e = new ExceptionMessage("401,Unauthorised",  false);
//			OutputStream out = response.getOutputStream();
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.writeValue(out, e);
//			out.flush();
	
	
	@Override
	  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
	          throws IOException, ServletException {
	      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	  }
}