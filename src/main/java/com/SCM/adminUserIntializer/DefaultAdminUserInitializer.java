package com.SCM.adminUserIntializer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.SCM.entities.User;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role;
import com.SCM.role.RoleRepository;

@Component
public class DefaultAdminUserInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAdminUserInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createAdminUserIfNotExists();
    }

    private void createAdminUserIfNotExists() {
        logger.info("Checking if admin user already exists...");

        // Check if an admin user already exists
        Optional<User> existingAdmin = userRepository.findByEmail("admin@gmail.com");

        if (existingAdmin.isPresent()) {
            logger.info("Admin user already exists. No need to create a new one.");
            return;
        }

        logger.info("Admin user does not exist. Creating a new admin user...");
        
     // Fetch the role from the database
        Role adminRole = roleRepository.findByName(Role.RoleType.ROLE_ADMIN);

        if (adminRole == null) {
            logger.error("Admin role not found in the database. Cannot create admin user.");
            return;
        }

        User adminUser = new User();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmail("admin@gmail.com");
        adminUser.setRole(adminRole); // Set the role for the admin user
        adminUser.setPassword(passwordEncoder.encode("123")); // Encode the password
       
        userRepository.save(adminUser);
        logger.info("Admin user created successfully.");
    }    
}