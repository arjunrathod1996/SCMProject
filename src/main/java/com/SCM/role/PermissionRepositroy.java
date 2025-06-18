package com.SCM.role;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepositroy extends CrudRepository<Permission, Long>{

}
