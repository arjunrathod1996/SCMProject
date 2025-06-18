package com.SCM.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.SCM.role.Role;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

//    @GetMapping
//    public ResponseEntity<List<Role.RoleType>> getRoles() {
//        List<Role.RoleType> roles = Arrays.asList(Role.RoleType.ROLE_MERCHANT_STAFF, Role.RoleType.ROLE_MERCHANT_ADMIN);
//        return ResponseEntity.ok(roles);
//    }
	
	@GetMapping
    public ResponseEntity<List<Map<String, String>>> getRoles() {
		
        List<Map<String, String>> roles = new ArrayList<>();
        for (Role.RoleType role : Arrays.asList(Role.RoleType.ROLE_MERCHANT_STAFF, Role.RoleType.ROLE_MERCHANT_ADMIN)) {
            Map<String, String> roleMap = new HashMap<>();
            roleMap.put("name", role.getName());
            roleMap.put("tag", role.getTag());
            roles.add(roleMap);
        }
        return ResponseEntity.ok(roles);
    }
}
