package com.SCM.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public interface MerchantRepository extends CrudRepository<Merchant, Long>{
	
	
	
//	@Query(value = "SELECT m FROM Merchant m "
//     		+ "Where m.active = true ")
//	public Page<Merchant> findMerchantPageWisee(Pageable pageable);
	
	@Query(value = "SELECT m FROM Merchant m WHERE m.active = true "
            + "AND (:businessID IS NULL OR m.business.id = :businessID ) ")
	public Page<Merchant> findMerchantPageWisee(Pageable pageable, @Param("businessID") Long businessID);


	public List<Merchant> findByName(String name);
	
	@Query(value = "SELECT m.* FROM merchant m JOIN region r ON m.region_id = r.id JOIN country c ON r.country_id = c.id WHERE c.name = :country AND r.state = :state AND r.city = :city", nativeQuery = true)
	List<Merchant> findMerchantsByLocation(@Param("country") String country, @Param("state") String state, @Param("city") String city);

	@Query(value = "SELECT * FROM merchant m "
			+ "Where m.short_link = :shortLink "
			+ "AND m.active = true ", nativeQuery = true)
	public Merchant findByShortLink(@Param("shortLink") String shortLink);
	
	@Query(value = "SELECT * FROM merchant m "
			+ "Where m.short_link = :shortLink "
			+ "AND m.active = true ", nativeQuery = true)
	public List<Merchant> findByShortLink_(@Param("shortLink") String shortLink);

	
	@Query(value = "SELECT * FROM Merchant as m "
			+ "WHERE m.active = true "
			+ "AND m.BUSINESS_ID = :businessID ",nativeQuery = true)
	public List<Merchant> findMerchantByActive(@Param("businessID") Long businessID);
	
	@Query("SELECT u.merchant.shortLink, u.staffLink FROM User u WHERE u.merchant.shortLink = :shortLink AND u.enabled = true")
    List<Object[]> findMerchantAndUserLinks(@Param("shortLink") String shortLink);


	public List<Merchant> findByBusiness(Business business);
	
	public List<Merchant> findByBusinessAndMainOutlet(Business business, boolean mainOutlet);

	
	@Query(value = "SELECT * FROM MERCHANT AS m "
			+ "WHERE m.BUSINESS_ID = :id ", nativeQuery = true)
	public List<Merchant> findMerchantByBusiness(@Param("id")Long id);
	
	//@Query(value = "SELECT m FROM merchant as m WHERE m.region IN (SELECT r.id FROM region as r WHERE r.country.name = :country AND r.state = :state AND r.city = :city)", nativeQuery = true)
   // public List<Merchant> findMerchantsByLocation(@Param("country") String country, @Param("state") String state, @Param("city") String city);

	//public List<Merchant> findMerchantsByLocation(String country, String state, String city);

}
  