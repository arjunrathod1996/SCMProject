package com.SCM.role;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RoleInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initializeRoles() {
        try {
            List<Role.RoleType> roleTypes = Arrays.asList(Role.RoleType.values());
            for (Role.RoleType roleType : roleTypes) {
                if (!roleRepository.existsByName(roleType)) {
                    Role role = new Role();
                    role.setName(roleType);
                    roleRepository.save(role);
                    System.out.println("Role " + roleType.name() + " created.");
                } else {
                    System.out.println("Role " + roleType.name() + " already exists.");
                }
            }
        } catch (Exception e) {
            System.err.println("🚨 Error initializing roles: " + e.getMessage());
            e.printStackTrace();
        }
    }


}