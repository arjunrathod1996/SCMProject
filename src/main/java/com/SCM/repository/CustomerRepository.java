package com.SCM.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.entities.Business;
import com.SCM.entities.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long>{

	Optional<Customer> findByEmail(String email);
	
	@Query(value = "select * from customer as m "
			+ "where m.email = :email ",nativeQuery =  true)
	public Customer findByEmail_(@Param("email") String email);

	List<Customer> findByMobileNumber(String mobileNumber);
	
	@Query(value = "select * from customer as m "
			+ "where m.mobile_Number = :mobileNumber ",nativeQuery =  true)
	public Customer findByPhoneNumber(@Param("mobileNumber") String mobileNumber);
	
	@Query(value = "select * from customer as m "
			+ "where m.mobile_Number = :mobileNumber ",nativeQuery =  true)
	 Optional<Customer> findByMobileNumbers(@Param("mobileNumber") String mobileNumber);
	
//	List<Customer> findByEmailContainingOrPhoneNumberContaining(String email, String phoneNumber);
//    List<Customer> findByBusinessAndEmailContainingOrPhoneNumberContaining(Business business, String email, String phoneNumber);
	
	@Query(value = "SELECT * FROM customer WHERE email LIKE %:email% OR mobile_number LIKE %:phoneNumber%", nativeQuery = true)
    List<Customer> findByEmailContainingOrPhoneNumberContaining(String email, String phoneNumber);
    
    @Query(value = "SELECT * FROM customer WHERE business_id = :businessId AND (email LIKE %:email% OR mobile_number LIKE %:phoneNumber%)", nativeQuery = true)
    List<Customer> findByBusinessAndEmailContainingOrPhoneNumberContaining(Long businessId, String email, String phoneNumber);
    
    @Query(value = "SELECT c.* " +
            "FROM customer_relation AS cr " +
            "JOIN customer AS c ON c.id = cr.customer_id " +
            "JOIN user AS u ON u.merchant_id = cr.merchant_id " +
            "WHERE u.id = ?1 AND u.merchant_id = ?2",
    countQuery = "SELECT COUNT(*) FROM customer_relation AS cr " +
                 "JOIN customer AS c ON c.id = cr.customer_id " +
                 "JOIN user AS u ON u.merchant_id = cr.merchant_id " +
                 "WHERE u.id = ?1 AND u.merchant_id = ?2",
    nativeQuery = true)
Page<Customer> findCustomersByUserIdAndMerchantId(Long userId, Long merchantId, Pageable pageable);

}
  