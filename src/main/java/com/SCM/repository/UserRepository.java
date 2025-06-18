package com.SCM.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.controllers.Merchant;
import com.SCM.entities.User;
import com.SCM.role.Role;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

    public Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM user WHERE provider_user_id = :providerUserId", nativeQuery = true)
    Optional<User> findByProviderUserId(@Param("providerUserId") String providerUserId);

    @Query(value = "SELECT * FROM user WHERE phone_number = :phoneNumber", nativeQuery = true)
    User findByPhoneNumber(@Param("phoneNumber")String phoneNumber);
    
    @Query(value = "SELECT * FROM user WHERE phone_number = :phoneNumber LIMIT 1", nativeQuery = true)
    Optional<User> findByPhoneNumber_(@Param("phoneNumber") String phoneNumber);

    
    @Query(value = "SELECT * FROM user WHERE email = :email", nativeQuery = true)
    public User findByUserName(@Param("email")String email);
	
	@Query(value = "SELECT u FROM User u "
     		+ "Where u.enabled = true ")
	public Page<User> findMerchantUserPageWisee(Pageable pageable);
	
	boolean existsByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.merchant.id IN :merchantIds")
	public List<User> findByRelatedMerchants(@Param("merchantIds") Long id);
	
	@Query(value = "SELECT * FROM User as u "
			+ "WHERE u.enabled = true "
			+ "AND u.MERCHANT_ID = :merchantID ",nativeQuery = true)
	public List<User> findMerchantByActive(@Param("merchantID") Long businessID);

	public User findByStaffLink(String staffLink);

	public List<User> findByMerchant(Merchant registrar);
	
	
	List<User> findByMerchantInAndRole(List<Merchant> merchants, Role role);

}
