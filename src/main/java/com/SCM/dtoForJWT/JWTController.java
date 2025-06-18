package com.SCM.dtoForJWT;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.entities.CustomUserDetails;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*@Controller
public class JWTController {
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService detailsService;
	
	
	@PostMapping("/token")
    public String authenticateAndGetToken(Model model,@ModelAttribute AuthRequest authRequest, HttpServletResponse res,HttpSession session) throws Exception {
	
	System.out.println("User Name     : " + authRequest.getUsername());
	System.out.println("User Password : " + authRequest.getPassword());
	
	try {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
	}catch (UsernameNotFoundException e) {
		session.setAttribute("message","Bad Credentials");
		return "redirect:/login";
	}catch (BadCredentialsException e) {
		session.setAttribute("message","Bad Credentials");
		return "redirect:/login";
	}
	try {
		final UserDetails userDetails = detailsService.loadUserByUsername(authRequest.getUsername());
		System.out.println("userDetails.getUsername: "   +userDetails.getUsername());
		final String token = jwtService.generateToken(userDetails);
		Cookie cookie = new Cookie("token",token);
		cookie.setMaxAge(Integer.MAX_VALUE);
		res.addCookie(cookie);
		System.out.println("token: " + token);
		return "redirect:/user/dashboard";
		}catch(Exception e)
		{
			session.setAttribute("messageg","Credentials were right But something went wrong!!");
			return "redirect:/login";
		}
	}

	@GetMapping("/log_out")
    public String logout(HttpServletRequest request,HttpServletResponse res,Model m,HttpSession session) {
		 String msg = null;
		 Cookie[] cookies2 = request.getCookies();
		 for(int i = 0; i < cookies2.length; i++) 
		 {
		 	if (cookies2[i].getName().equals("token")) 
		 	{
		     cookies2[i].setMaxAge(0);
		     res.addCookie(cookies2[i]);
		 		msg = "Logout successfully";
		  }
	    }
		 session.setAttribute("message", msg);
	        return "redirect:/login";
		}
}*/


@Controller
public class JWTController {
    
    @Autowired
    private JWTService jwtService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService detailsService;
    
    @Autowired
    private HttpSession session;
    
    @Autowired
    private UserRepository userRepository;
    
//    @PostMapping("/token")
//    public String authenticateAndGetToken(Model model, @ModelAttribute AuthRequest authRequest, HttpServletResponse res) throws Exception {
//        
//        try {
//            // Authenticate user using provided credentials
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//        } catch (UsernameNotFoundException | BadCredentialsException e) {
//            // Handle authentication failure
//            session.setAttribute("message", "Bad Credentials");
//            return "redirect:/login";
//        }
//        
//        // Load user details
//        UserDetails userDetails = detailsService.loadUserByUsername(authRequest.getUsername());
//        
//        // Generate JWT token
//        String token = jwtService.generateToken(userDetails);
//        
//        // Store token in cookie
//        Cookie cookie = new Cookie("token", token);
//        cookie.setMaxAge(Integer.MAX_VALUE); // Set cookie expiration
//        res.addCookie(cookie);
//        
//        return "redirect:/user/dashboard";
//    }
    
    @PostMapping("/token")
    public String authenticateAndGetToken(Model model, @ModelAttribute AuthRequest authRequest, HttpServletResponse res) throws Exception {
    	
    	User user = null;
        
        try {
            // Authenticate user using provided credentials
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Handle authentication failure
            return "redirect:/login?error=true";
        }
        
        // Load user details
        UserDetails userDetails = detailsService.loadUserByUsername(authRequest.getUsername());
        
        // Generate JWT token
        String token = jwtService.generateToken(userDetails);
        
        // Store token in cookie
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(Integer.MAX_VALUE); // Set cookie expiration
        res.addCookie(cookie);
        
       
        
        if(userRepository.findByEmail(userDetails.getUsername()).get().getRole().getName().name() == "ROLE_CUSTOMER") {
        	return "redirect:/my_app/access";
        }
        
        return "redirect:/user/dashboard";
    }


    @GetMapping("/log_out")
    public String logout(HttpServletRequest request, HttpServletResponse res) {
        // Invalidate token by setting cookie expiration to 0
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    cookie.setMaxAge(0);
                    res.addCookie(cookie);
                }
            }
        }
        
        session.setAttribute("message", "Logout successfully");
        return "redirect:/login";
    }
}

