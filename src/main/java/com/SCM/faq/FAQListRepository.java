package com.SCM.faq;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.SCM.entities.Business;

@Repository
public interface FAQListRepository extends CrudRepository<FAQList, Long>{
	List<FAQList> findByBusinessIdOrderByOrderDesc(Long businessId);

	@Query(value = "SELECT f FROM FAQList f")
	Page<FAQList> findFaqListPageWise(Pageable pageable);
	
//	@Query(value ="SELECT * FROM FAQ_LIST AS f "
//			+ "WHERE f.BUSINESS_ID = :businessID ", nativeQuery = true)
//	public List<FAQList> getAllFAQList(Business business);
	
	
	public List<FAQList> findByBusiness(Business business);
}
