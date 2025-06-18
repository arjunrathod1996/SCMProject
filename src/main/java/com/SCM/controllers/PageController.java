package com.SCM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.SCM.entities.User;
import com.SCM.forms.UserForm;
import com.SCM.role.Role;
import com.SCM.role.RoleRepository;
import com.SCM.service.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
public class PageController {
	
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserServiceImpl userService;	

	
	@GetMapping("/home")
	public String home(Model model) {
		 
		model.addAttribute("hideLinks", false); 
		return "home";
	}

	@GetMapping("/")
	public String index() {
		return "redirect:/home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		return "about";
	}

	@GetMapping("/services")
	public String services(Model model) {
		return "services";
	}
	
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("hideLinks", true);
		return "login";
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("hideLinks", true);
		UserForm userForm = new UserForm();
		model.addAttribute("userForm", userForm);
		return "register";
	}

	@PostMapping("/do_register")
	public String doRegister(@ModelAttribute UserForm userForm, HttpSession session) {
	    try {
	        // Check if passwords match
	        if (!userForm.getPassword().equals(userForm.getRepeatPassword())) {
	            session.setAttribute("message", "Passwords do not match.");
	            session.setAttribute("alertType", "error");
	            return "redirect:/register"; // Redirect back to registration page
	        }
	
	        // If passwords match, proceed with registration
	        User user = new User();
	        user.setLastName(userForm.getLastName());
	        user.setFirstName(userForm.getFirstName());
	        user.setEmail(userForm.getEmail());
	        user.setPassword(userForm.getPassword());
	        user.setPhoneNumber(userForm.getPhoneNumber()); 
	        
	        Role userRole = roleRepository.findByName(Role.RoleType.ROLE_CUSTOMER);
	        if (userRole == null) {
	            userRole = new Role();
	            userRole.setName(Role.RoleType.ROLE_CUSTOMER);
	            userRole = roleRepository.save(userRole);
	        }
	        user.setRole(userRole);
	        userService.saveUser(user); // Save the user using a service layer
	        session.setAttribute("message", "Registration successful!");
	        session.setAttribute("alertType", "success");
	    } catch (Exception e) {
	        session.setAttribute("message", "Something went wrong. Please try again.");
	        session.setAttribute("alertType", "error");
	    }
	    return "redirect:/register";
	}

	 @GetMapping("/logout")
	 public String logout(HttpServletRequest request, HttpServletResponse response) {
	     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	     if (auth != null) {
	         new SecurityContextLogoutHandler().logout(request, response, auth);
	     }
	     return "redirect:/login?logout=true";
	 }

    @GetMapping("/protected-page")
    public String protectedPage() {
        return "redirect:/login"; // Redirect to login page if user tries to access protected page after logout
    }
	
}
