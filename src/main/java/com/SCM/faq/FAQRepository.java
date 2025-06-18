package com.SCM.faq;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends CrudRepository<FAQ, Long>{
	
	@Query(value = "SELECT *  FROM FAQ f "
			+ "WHERE f.deleted = false "
			+ "AND f.business_id =:businessID "
			+ "ORDER BY f.order DESC",nativeQuery = true)
	List<FAQ> fingFaqList(@Param("businessID") Long businessID);

	List<FAQ> findByFaqListId(Long id);
	
	
	@Query(value = "SELECT fl.id, fl.topic, GROUP_CONCAT(fc.question), GROUP_CONCAT(fc.answer) " +
            "FROM FAQ_List fl " +
            "JOIN FAQ fc "
            +"ON fl.id = fc.faq_List_id " +
            "WHERE fl.business_id = :businessId "
            + "AND fc.deleted = false " +
            "GROUP BY fl.id "
            + "order by fl.order desc , fc.order asc", nativeQuery = true)
	public List<Object[]> findAllFAQsByBusinessId(Long businessId);

	List<FAQ> findByFaqListIdOrderByOrder(Long id);

	@Query(value = "SELECT f FROM FAQ f ")
	Page<FAQ> findFaqPageWise(Pageable pageable);

}
