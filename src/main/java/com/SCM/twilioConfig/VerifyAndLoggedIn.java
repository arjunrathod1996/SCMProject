package com.SCM.twilioConfig;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.SCM.dtoForJWT.AuthRequest;
import com.SCM.dtoForJWT.JWTService;
import com.SCM.entities.User;
import com.SCM.repository.UserRepository;
import com.SCM.service.SecuritycCustomerUserDetailService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class VerifyAndLoggedIn {
	
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

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private TwilioService twilioService;
    
    
    @Autowired
    SecuritycCustomerUserDetailService customerUserDetailService;
    
    
//    @PostMapping("/verify-otp")
//    public void verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String phoneNumber = verifyOtpRequest.getPhoneNumber();
//        String otp = verifyOtpRequest.getOtp();
//        System.out.println(phoneNumber + ": " + otp);
//        User user = userRepository.findByPhoneNumber(phoneNumber);
//        System.out.println(user.getId());
//        if (user != null && user.getOtp().equals(otp)) {
//            UserDetails userDetails = customerUserDetailService.loadUserByPhoneNumber(phoneNumber);
//            System.out.println(userDetails);
//            UsernamePasswordAuthenticationToken  authenticationToken= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            System.out.println(authenticationToken);
//            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            String token = jwtService.generateToken(userDetails);
//            System.out.println(token);
//            Cookie cookie = new Cookie("token", token);
//            cookie.setMaxAge(Integer.MAX_VALUE); // Set cookie expiration
//            response.addCookie(cookie);
//            response.sendRedirect("/my_app/access");
//        } else {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid OTP");
//        }
//    }
    
    
    // @PostMapping("/verify-otp")
    // public String verifyOtp(@ModelAttribute VerifyOtpRequest verifyOtpRequest) {
    //     String phoneNumber = verifyOtpRequest.getPhoneNumber();
    //     String otp = verifyOtpRequest.getOtp();
        
    //     System.out.println("otp >> : " + otp);
        
    //     // Retrieve user by phone number
    //     User user = userRepository.findByPhoneNumber(phoneNumber);
        
    //     if (user != null && user.getOtp().equals(otp)) {
    //         // OTP is valid, proceed with authentication
        	
    //     	System.out.println(" >>>>>>>>>>>>>>>>>>>>>");
        	
    //         UserDetails userDetails = customerUserDetailService.loadUserByUsername(phoneNumber);
            
    //         System.out.println("userDetails : " + userDetails);
            
    //         // Authenticate user
    //         try {
    //             authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
                
    //             // Authentication successful, generate JWT token
    //             String token = jwtService.generateToken(userDetails);
                
    //             System.out.println(" >>>>>>>>> : token : " + token);
                
    //             // Return token
    //             return "redirect:/my_app/access";
    //         } catch (BadCredentialsException e) {
    //             // Handle authentication failure
    //             return "Invalid credentials";
    //         }
    //     } else {
    //         // OTP is invalid
    //         return "Invalid OTP";
    //     }
    // }
    


//   @PostMapping("/verify-otp")
//   public String authenticateAndGetToken(Model model, @ModelAttribute VerifyOtpRequest verifyOtpRequest, HttpServletResponse res) throws Exception {
//     
//   	System.out.println(verifyOtpRequest.getPhoneNumber());
//   	System.out.println(verifyOtpRequest.getOtp());
//   	
//       // Check if OTP is correct
//       if (isOtpValid(verifyOtpRequest.getPhoneNumber(), verifyOtpRequest.getOtp())) {
//           // OTP is valid, proceed with authentication and token generation
//        //    try {
//        //        // Authenticate user using provided credentials
//        //        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(verifyOtpRequest.getPhoneNumber(), verifyOtpRequest.getOtp()));
//        //    } catch (UsernameNotFoundException | BadCredentialsException e) {
//        //        // Handle authentication failure
//        //        session.setAttribute("message", "Bad Credentials");
//        //        return "redirect:/my_app/login";
//        //    }
//           
//           // Load user details
//           UserDetails userDetails = detailsService.loadUserByUsername(verifyOtpRequest.getPhoneNumber());
//           
//           System.out.println(" >>>>>>>>>>>>> : " + userDetails);
//           
//           // Generate JWT token
//           String token = jwtService.generateToken(userDetails);
//
//           System.out.println(" >>>>>>>>>>> :" + token);
//           
//           // Store token in cookie
//           Cookie cookie = new Cookie("token", token);
//           cookie.setMaxAge(Integer.MAX_VALUE); // Set cookie expiration
//           res.addCookie(cookie);
//           
//           return "redirect:/my_app/access";
//       } else {
//       	
//       	System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//       	
//           // OTP is invalid, redirect to login page
//           return "redirect:/my_app/login";
//       }
//   }
//
//   private boolean isOtpValid(String phoneNumber, String otp) {
//       // Implement logic to validate OTP
//       User user = userRepository.findByPhoneNumber(phoneNumber);
//       return user != null && user.getOtp().equals(otp);
//   }
    
    
//    @PostMapping("/verify-otp")
//    public String authenticateAndGetToken(Model model, @ModelAttribute VerifyOtpRequest verifyOtpRequest , HttpServletResponse res) throws Exception {
//        
//        try {
//            // Authenticate user using provided credentials
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(verifyOtpRequest.getPhoneNumber() , verifyOtpRequest.getOtp()));
//        } catch (UsernameNotFoundException | BadCredentialsException e) {
//            // Handle authentication failure
//            session.setAttribute("message", "Bad Credentials");
//            return "redirect:/login";
//        }
//        
//        // Load user details
//        UserDetails userDetails = detailsService.loadUserByUsername(verifyOtpRequest.getPhoneNumber());
//        
//        // Generate JWT token
//        String token = jwtService.generateToken(userDetails);
//        
//        System.out.println(" >>>>>>>>>>> : " + token);
//        
//        // Store token in cookie
//        Cookie cookie = new Cookie("token", token);
//        cookie.setMaxAge(Integer.MAX_VALUE); // Set cookie expiration
//        res.addCookie(cookie);
//        
//        return "redirect:/user/dashboard";
//    }


}
