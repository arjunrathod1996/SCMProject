package com.SCM.cartItem;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.leads.LeadActivity;
import com.SCM.leads.LeadActivity.ActivityType;

@Repository
public interface BillingSummaryRepository extends JpaRepository<BillingSummary, Long> {
	
	
	
//	@Query(value = "SELECT b FROM BillingSummary b "
//			+ "JOIN b.merchant m "
//			+ "WHERE b.merchant.id = :merchantId "
//			+ "AND m.business.id = :businessId ")
////			+ "AND ( (:startDate IS NULL AND :endDate IS NULL ) OR (DATE(b.creationTime) "
////			+ "BETWEEN DATE(STR_TO_DATE(:startDate,'%Y-%m-%d') ) AND DATE(STR_TO_DATE(:endDate,'%Y-%m-%d') ) ) ) ")
//	Page<BillingSummary> findByBillByPageWise(Pageable pageable,
//											  @Param("businessId") Long businessId,
//											  @Param("merchantId") Long merchantId
//											
//											 /* @Param("startDate") String startDate,
//											  @Param("endDate") String endDate*/
//											  );
	
	
	@Query("select bs from BillingSummary bs WHERE (:businessId is null or bs.merchant.business.id = :businessId) "
			+ "AND (:userId is null or bs.user.id = :userId) ")
	Page<BillingSummary> findBillByPageWise(Pageable pageable, @Param("businessId") Long businessId, @Param("userId") Long userId);
	
	
	
	 // Fetch by merchant ID
	@Query("SELECT b FROM BillingSummary b "
			+ "WHERE b.merchant.id = :merchantId")
    List<BillingSummary> findByMerchantId(Long merchantId);

	// Fetch by multiple merchant IDs
    @Query("SELECT b FROM BillingSummary b WHERE b.merchant.id IN :merchantIds")
    List<BillingSummary> findByMerchantIds(@Param("merchantIds") List<Long> merchantIds);
	
}

