package com.SCM.twilioConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.dtoForJWT.AuthRequest;
import com.SCM.dtoForJWT.JWTAuthFilter;
import com.SCM.dtoForJWT.JWTService;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role;
import com.SCM.role.RoleRepository;
import com.SCM.service.SecuritycCustomerUserDetailService;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;




@RestController
@RequestMapping("/auth")
public class OTPController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private HttpSession session;
    
    @Autowired
    SecuritycCustomerUserDetailService customerUserDetailService;
    
    @Autowired
    private UserDetailsService detailsService;
    
    @Autowired
    UsersVerificationRepository usersVerificationRepository;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    CustomerRepository customerRepository;
    
    
    
    private static final Logger logger = LoggerFactory.getLogger(OTPController.class);
    
    
    /*@PostMapping("/generate-otp/{mobileNumber}")
    public ResponseEntity<String> authenticateAndGetGeneratedOtp(@PathVariable String mobileNumber) {
        // Generate a random 4-digit OTP
        String otp = generateOTP();

        // Check if an entry already exists for the given mobile number
        Optional<UsersVerification> existingVerification = usersVerificationRepository.findByContentAndContentType(mobileNumber, UsersVerification.ContentType.MOBILE_NUMBER);

        UsersVerification usersVerification;
        if (existingVerification.isPresent()) {
            // Update the existing entry with the new OTP
            usersVerification = existingVerification.get();
            usersVerification.setVerficationCode(otp);
            usersVerification.setStatus(UsersVerification.Status.ACTIVE); // Reset status to active if it was closed
        } else {
            // Create a new entry
            usersVerification = new UsersVerification();
            usersVerification.setContent(mobileNumber);
            usersVerification.setVerficationCode(otp);
            usersVerification.setContentType(UsersVerification.ContentType.MOBILE_NUMBER);
            usersVerification.setStatus(UsersVerification.Status.ACTIVE);
        }
        // Save the UsersVerification instance
        usersVerificationRepository.save(usersVerification);
        // Send the OTP in the response
        String responseMessage = "OTP generated successfully for mobile number: " + otp;
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    // Method to generate a random 4-digit OTP
    private String generateOTP() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // Generate a random number between 1000 and 9999
        return String.valueOf(otp);
    }*/
    
    
    
//    @PostMapping("/verify-otp/{mobileNumber}/{otp}")
//    public ResponseEntity<String> verifyOtp(@PathVariable String mobileNumber, @PathVariable String otp) {
//        // Check if an entry exists in UsersVerification table for the given mobile number and OTP
//        UsersVerification usersVerification = usersVerificationRepository.findByContentAndVerficationCodeAndStatus(mobileNumber, otp, UsersVerification.Status.ACTIVE);
//
//        if (usersVerification != null) {
//            // OTP matched, proceed with login
//            // Update UsersVerification status to CLOSED or delete the entry if not needed anymore
//            usersVerification.setStatus(UsersVerification.Status.CLOSED);
//            usersVerificationRepository.save(usersVerification);
//            return ResponseEntity.status(HttpStatus.OK).body("OTP verified successfully. Login successful.");
//        } else {
//            // OTP didn't match
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect OTP. Please try again.");
//        }
//    }
    
    
    @PostMapping("/otp-authenticate")
    public ResponseEntity<?> createOtpAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        UserDetails userDetails = ((SecuritycCustomerUserDetailService) detailsService).loadUserByMobileAndOtp(authenticationRequest.getMobileNumber());
        final String jwt = jwtService.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }


    
    
   /* @PostMapping("/generate-otp/{mobileNumber}")
    public ResponseEntity<String> authenticateAndGetGeneratedOtp(@PathVariable String mobileNumber) {
        // Generate a random 4-digit OTP
        String otp = generateOTP();
        
        // Retrieve the user by mobile number
        User user = userRepository.findByPhoneNumber(mobileNumber);
        
        Role role = roleRepository.findByName(Role.RoleType.ROLE_CUSTOMER);
        
        // If user does not exist, create a new user
        if (user == null) {
            user = new User();
            user.setPhoneNumber(mobileNumber);
            user.setRole(role);
            userRepository.save(user); // Save the new user
        }
        

        // Check if the UsersVerification entry already exists for the given mobile number
        UsersVerification usersVerification = usersVerificationRepository.findByContentAndStatus(mobileNumber, UsersVerification.Status.ACTIVE);
        
        // If entry does not exist, create a new one
        if (usersVerification == null) {
            usersVerification = new UsersVerification();
            usersVerification.setContent(mobileNumber);
            usersVerification.setUser(user); // Set the user
        }
        
        // Update the OTP and status
        usersVerification.setVerficationCode(otp);
        usersVerification.setStatus(UsersVerification.Status.ACTIVE);

        // Save the UsersVerification instance
        usersVerificationRepository.save(usersVerification);

        // Send the OTP in the response
        String responseMessage = "OTP generated successfully for mobile number: " + otp;
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    // Method to generate a random 4-digit OTP
    private String generateOTP() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // Generate a random number between 1000 and 9999
        return String.valueOf(otp);
    }*/  
    
    
//    @PostMapping("/generate-otp/{mobileNumber}")
//    public ResponseEntity<String> authenticateAndGetGeneratedOtp(@PathVariable String mobileNumber) {
//        // Generate a random 4-digit OTP
//        String otp = generateOTP();
//        
//        // Retrieve the user by mobile number
//        User user = userRepository.findByPhoneNumber(mobileNumber);
//        
//        // Retrieve the role for the customer
//        Role role = roleRepository.findByName(Role.RoleType.ROLE_CUSTOMER);
//        
//        // If user does not exist, create a new user with customer
//        if (user == null) {
//            // Create a new user
//            user = new User();
//            user.setPhoneNumber(mobileNumber);
//            user.setRole(role);
//            userRepository.save(user); // Save the new user
//            
//            // Create a new customer
//            Customer customer = new Customer();
//            customer.setMobileNumber(mobileNumber);
//            // Set other customer properties if needed
//            // For example:
//            // customer.setFirstName(firstName);
//            // customer.setLastName(lastName);
//            // customer.setEmail(email);
//            // customer.setCountry(country);
//            // customer.setRegion(region);
//            // ...
//            // Save the new customer
//            customerRepository.save(customer);
//            
//            // Link the customer to the user
//            user.setCustomer(customer);
//            userRepository.save(user); // Update the user
//        }
//
//        // Check if the UsersVerification entry already exists for the given mobile number
//        UsersVerification usersVerification = usersVerificationRepository.findByContentAndStatus(mobileNumber, UsersVerification.Status.ACTIVE);
//        
//        // If entry does not exist, create a new one
//        if (usersVerification == null) {
//            usersVerification = new UsersVerification();
//            usersVerification.setContent(mobileNumber);
//            usersVerification.setUser(user); // Set the user
//        }
//        
//        // Update the OTP and status
//        usersVerification.setVerficationCode(otp);
//        usersVerification.setStatus(UsersVerification.Status.ACTIVE);
//
//        // Save the UsersVerification instance
//        usersVerificationRepository.save(usersVerification);
//
//        // Send the OTP in the response
//        String responseMessage = "OTP generated successfully for mobile number: " + otp;
//        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
//    }
    
    
    @PostMapping("/generate-otp/{mobileNumber}")
    public ResponseEntity<String> authenticateAndGetGeneratedOtp(@PathVariable String mobileNumber) {
        // Generate a random 4-digit OTP
        String otp = generateOTP();

        // Retrieve the user by mobile number
        User user = userRepository.findByPhoneNumber(mobileNumber);

        // Retrieve the role for the customer
        Role role = roleRepository.findByName(Role.RoleType.ROLE_CUSTOMER);

        // If user does not exist, create a new user with customer
//        if (user == null) {
//            // Create a new user
//            user = new User();
//            user.setPhoneNumber(mobileNumber);
//            user.setRole(role);
//            userRepository.save(user); // Save the new user
//
//            // Create a new customer
//            Customer customer = new Customer();
//            customer.setMobileNumber(mobileNumber);
//            // Set other customer properties if needed
//            // For example:
//            // customer.setFirstName(firstName);
//            // customer.setLastName(lastName);
//            // customer.setEmail(email);
//            // customer.setCountry(country);
//            // customer.setRegion(region);
//            // ...
//            // Save the new customer
//            customerRepository.save(customer);
//
//            // Link the customer to the user
//            user.setCustomer(customer);
//            userRepository.save(user); // Update the user
//        }
        
        
        if (user == null) {
            // Create a new user
            user = new User();
            user.setPhoneNumber(mobileNumber);
            user.setRole(role);
            userRepository.save(user); // Save the new user

            // Check if a customer with the same mobile number already exists
            Optional<Customer> existingCustomerOpt = customerRepository.findByMobileNumbers(mobileNumber);
            
            if (existingCustomerOpt.isEmpty()) {
                // No existing customer found, create a new customer
                Customer customer = new Customer();
                customer.setMobileNumber(mobileNumber);
                // Set other customer properties if needed
                // For example:
                // customer.setFirstName(firstName);
                // customer.setLastName(lastName);
                // customer.setEmail(email);
                // customer.setCountry(country);
                // customer.setRegion(region);
                // ...
                // Save the new customer
                customerRepository.save(customer);

                // Link the customer to the user
                user.setCustomer(customer);
            } else {
                // Existing customer found, link it to the new user
                user.setCustomer(existingCustomerOpt.get());
            }
            userRepository.save(user); // Update the user
        }


        // Check if the UsersVerification entry already exists for the given user
        Optional<UsersVerification> existingVerification = usersVerificationRepository.findByContentAndContentType(mobileNumber, UsersVerification.ContentType.MOBILE_NUMBER);

        if (existingVerification.isPresent()) {
            // Update the existing verification
            UsersVerification usersVerification = existingVerification.get();
            usersVerification.setVerficationCode(otp);
            usersVerification.setStatus(UsersVerification.Status.ACTIVE);
            usersVerificationRepository.save(usersVerification);
        } else {
            // Create a new UsersVerification entry
            UsersVerification newVerification = new UsersVerification();
            newVerification.setContent(mobileNumber);
            newVerification.setUser(user); // Set the user
            newVerification.setVerficationCode(otp);
            newVerification.setStatus(UsersVerification.Status.ACTIVE);
            usersVerificationRepository.save(newVerification);
        }

        // Send the OTP in the response
        String responseMessage = "OTP generated successfully for mobile number: " + otp;
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }




    private String generateOTP() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // Generate a random number between 1000 and 9999
        return String.valueOf(otp);
    }
    
    
   /* @PostMapping("/verify-otp/{mobileNumber}/{otp}")
    public ResponseEntity<String> verifyOtp(@PathVariable String mobileNumber, @PathVariable String otp, HttpServletResponse res) throws IOException {
        UsersVerification usersVerification = usersVerificationRepository.findByContentAndVerficationCodeAndStatus(mobileNumber, otp, UsersVerification.Status.ACTIVE);

        if (usersVerification != null) {
            usersVerification.setStatus(UsersVerification.Status.CLOSED);
            usersVerificationRepository.save(usersVerification);

            User user = usersVerification.getUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found");
            }
            UserDetails userDetails = customerUserDetailService.loadUserByMobileAndOtp(user.getPhoneNumber());
            
            String token = jwtService.generateToken(userDetails);
           
            if (token != null) {
                logger.info("JWT token generated successfully for user: {}", token);
            } else {
                logger.error("Failed to generate JWT token for user: {}", userDetails.getUsername());
                throw new RuntimeException("Failed to generate JWT token for user");
            }
            
            setCookie(res, "token", token, Integer.MAX_VALUE, true);

            res.sendRedirect("/my_app/access");

            
            
            System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : ");

            return ResponseEntity.status(HttpStatus.OK).body("OTP verified successfully. Login successful.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect OTP. Please try again.");
        }
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setPath("/"); // Ensure the path is set to root
        response.addCookie(cookie);
    }*/
    
    
    @PostMapping("/verify-otp/{mobileNumber}/{otp}")
    public ResponseEntity<Void> verifyOtp(@PathVariable String mobileNumber, @PathVariable String otp, HttpServletResponse res) throws IOException {
        UsersVerification usersVerification = usersVerificationRepository.findByContentAndVerficationCodeAndStatus(mobileNumber, otp, UsersVerification.Status.ACTIVE);

        if (usersVerification != null) {
            usersVerification.setStatus(UsersVerification.Status.CLOSED);
            usersVerificationRepository.save(usersVerification);

            User user = usersVerification.getUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            UserDetails userDetails = customerUserDetailService.loadUserByMobileAndOtp(user.getPhoneNumber());

            String token = jwtService.generateToken(userDetails);

            if (token != null) {
                logger.info("JWT token generated successfully for user: {}", token);
            } else {
                logger.error("Failed to generate JWT token for user: {}", userDetails.getUsername());
                throw new RuntimeException("Failed to generate JWT token for user");
            }

            setCookie(res, "token", token, Integer.MAX_VALUE, true);

            // Setting Location header for redirection
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/my_app/access"));
            return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setPath("/"); // Ensure the path is set to root
        response.addCookie(cookie);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

//    @PostMapping("/generate-otp")
//    public String generateOtp(@RequestBody OTPRequest otpRequest) {
//        String phoneNumber = otpRequest.getPhoneNumber();
//        User user = userRepository.findByPhoneNumber(phoneNumber);
//        if (user == null) {
//            user = new User();
//            user.setPhoneNumber(phoneNumber);
//        }
//        String otp = String.valueOf((int)(Math.random() * 9000) + 1000);
//        System.out.println("Generated OTP: " + otp);
//        user.setOtp(otp);
//        user.setPassword(passwordService.encodePassword(otp)); 
//        //user.setPassword(otp); //
//        userRepository.save(user);
//        twilioService.sendOtp(phoneNumber, otp);
//        return "OTP sent";
//    }
    

    
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
    
//    @PostMapping("/verify-otp")
//    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest, HttpServletRequest request, HttpServletResponse response) {
//        String phoneNumber = verifyOtpRequest.getPhoneNumber();
//        String otp = verifyOtpRequest.getOtp();
//        User user = userRepository.findByPhoneNumber(phoneNumber);
//        
//        if (user != null && user.getOtp().equals(otp)) {
//          //  UserDetails userDetails = customerUserDetailService.loadUserByPhoneNumber(phoneNumber);
//          //  UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//         //   authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//         //   SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//         //   String token = jwtService.generateToken(userDetails);
//         //   Cookie cookie = new Cookie("token", token);
//          //  cookie.setMaxAge(Integer.MAX_VALUE); // Set cookie expiration
//          //  response.addCookie(cookie);
//         //   return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Invalid OTP\"}");
//        }
//    }

    
    
  

//    @PostMapping("/set-password")
//    public String setPassword(@RequestBody SetPasswordRequest setPasswordRequest) {
//        String phoneNumber = setPasswordRequest.getPhoneNumber();
//        String password = setPasswordRequest.getPassword();
//        User user = userRepository.findByPhoneNumber(phoneNumber);
//        if (user != null) {
//            user.setPassword(passwordService.encodePassword(password));
//            // userRepository.save(user);
//            return "Password set";
//        }
//        return "User not found";
//    }
//
//    @PostMapping("/login")
//    public String login(@RequestBody LoginRequest loginRequest) {
//        String phoneNumber = loginRequest.getPhoneNumber();
//        String password = loginRequest.getPassword();
//        User user = userRepository.findByPhoneNumber(phoneNumber); 
//        if (user != null && passwordService.matches(password, user.getPassword())) {
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(phoneNumber, password));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            String token = jwtService.generateToken(phoneNumber);
//            return "Login successful. Token: " + token;
//        }
//        return "Invalid credentials";
//    }
    
    
    
}


