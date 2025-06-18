package com.SCM.relation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Customer;

@Repository
public interface CustomerRelationRepository extends CrudRepository<CustomerRelation, Long>{

	
	@Query("SELECT cr FROM CustomerRelation cr WHERE cr.merchant.id = :merchantId AND cr.merchant.business.id = :businessId AND cr.customer.mobileNumber IN :mobileNumbers")
	List<CustomerRelation> findCustomerRelationsByMerchantAndBusinessAndMobileNumbers(
	    @Param("merchantId") Long merchantId,
	    @Param("businessId") Long businessId,
	    @Param("mobileNumbers") List<String> mobileNumbers
	);

	List<CustomerRelation> findByCustomerAndMerchantIn(Customer referred, List<Merchant> relatedMerchants);

	List<CustomerRelation> findByCustomerAndMerchant(Customer customer, Merchant merchant);
	
	 Optional<CustomerRelation> findByMerchantAndCustomer(Merchant merchant, Customer customer);
	 
	 @Query(value = "SELECT cr.* FROM customer_relation cr " +
             "JOIN merchant m ON cr.merchant_id = m.id "
             + "JOIN customer as c "
             + "ON c.id  = cr.customer_id " +
             "WHERE m.business_id = ?1 " +
             "AND c.email = ?2 " +
             "AND (c.email LIKE %?3% OR c.mobile_number LIKE %?3%)",
     nativeQuery = true)
List<CustomerRelation> findByMerchantBusinessIdAndMerchantEmailAndCustomerEmailOrMobileNumber(
  Long businessId, String merchantEmail, String query);

	 
	 @Query(value = "SELECT * FROM customer_relation as cr join customer as c on c.id = cr.customer_id "
				+ " WHERE c.email LIKE %:email% OR c.mobile_number LIKE %:phoneNumber%", nativeQuery = true)
	    List<CustomerRelation> findByEmailContainingOrPhoneNumberContaining(String email, String phoneNumber);



}
