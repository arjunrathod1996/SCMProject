package com.SCM.role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.entities.Business;
import com.SCM.role.Role.RoleType;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
	
	 boolean existsByName(Role.RoleType name);
	 
	 public Role findByName(Role.RoleType name);
	 
	 @Query(value = "SELECT * FROM ROLE WHERE role_name = :name", nativeQuery = true)
	    Role findByRoleName(@Param("name") String name);
	 
//	 @Query(value = "SELECT * FROM role WHERE role_name = ?1", nativeQuery = true)
//	    List<Role> findByRoleName(Role.RoleType roleName);
	 
	 @Query(value = "SELECT * FROM role WHERE role_name = ?1", nativeQuery = true)
	    List<Role> findByRoleName_(@Param("name") String name);
	 
	 
	 
	 
	 
}
