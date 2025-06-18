package com.SCM.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.SCM.entities.Business;

@Repository
public interface BusinessRepository extends CrudRepository<Business,Long>{

     @Query(value = "SELECT b FROM Business b "
     		+ "Where b.deleted = false ")
	public Page<Business> findBusinessPageWisee(Pageable pageable);

	public List<Business> findByName(String name);  

}
 