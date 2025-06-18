package com.SCM.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.SCM.controllers.Country;
import com.SCM.controllers.Region;

@Repository
public interface RegionRepository extends CrudRepository<Region, Long>{
	
	@Query(value = "SELECT r FROM Region r ")
	public Page<Region> findRegionPageWise(Pageable pageable);

	public List<Region> findByCity(String name);

}
