package com.SCM.role;

import java.util.List;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SCM.role.Role.RoleType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;


@Service
public class RoleService {
	
	@Autowired
	RoleRepository roleRepository;
	@Autowired
    private EntityManager entityManager;
	
	public List<Role> getRolesByRoleName(RoleType roleName) {
        // Using the repository method
        List<Role> roles = roleRepository.findByRoleName_(roleName.name());
        System.out.println("Roles from repository: " + roles);

        // Using direct EntityManager query
        roles = findByRoleNameDirect(roleName);
        System.out.println("Roles from EntityManager: " + roles);

        return roles;
    }
	
	 public List<Role> findByRoleNameDirect(RoleType roleName) {
	        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :roleName", Role.class);
	        query.setParameter("roleName", roleName);
	        return query.getResultList();
	    }
	
//	 public List<Role> findByRoleNameDirect(Role.RoleType roleName) {
//	        Query query = entityManager.createNativeQuery("SELECT * FROM role WHERE role_name = ?1", Role.class);
//	        query.setParameter(1, roleName.name());
//	        return query.getResultList();
//	    }
}
