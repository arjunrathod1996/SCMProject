package com.SCM.photo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.controllers.Merchant;
import com.SCM.photo.Photo.PhotoType;

@Repository
public interface PhotoMerchantRepository extends CrudRepository<PhotoMerchant, Long>{

public Photo findByFileNameAndDeleted(String fileName, boolean deleted);
	
	List<Photo> findByMerchantAndDeletedOrderBySequence(Merchant merchant, boolean b);

	List<Photo> findByMerchantAndTypeAndDeletedOrderBySequence(Merchant merchant, PhotoType type, boolean b);
	
	public Photo findByMerchantAndTypeAndSequenceAndDeleted(Merchant merchant, PhotoType type, Long sequence, boolean deleted);
	
	@Query(value = "SELECT COALESCE(COUNT(p.id),0) FROM Photo p "
			+ "WHERE p.deleted = false "
			+ "AND p.type = :photoType "
			+ "AND p.merchant.id = :merchantID ")
	public Integer countByMerchant(@Param("merchantID") Long merchantID,@Param("photoType") PhotoType pwaLogoS);
	
	@Query("SELECT p FROM Photo p "
	           + "WHERE p.deleted = false "
	           + "AND p.type = :type "
	           + "AND p.id = :photoId "
	           + "AND p.merchant IS NULL "
	           + "ORDER BY p.sequence")
	    Optional<Photo> findByPhotoIdAndType(@Param("photoId") Long photoId, @Param("type") PhotoType customerProfile);

	public List<PhotoMerchant> findByMerchantIdAndUserId(Long merchantID, Long merchantStaffLink);

	public List<PhotoMerchant> findByMerchantId(Long merchantID);

	public List<PhotoMerchant> findByTypeAndDeletedFalse(com.SCM.photo.PhotoMerchant.PhotoType type);

	public List<PhotoMerchant> findByMerchantIdAndDeletedFalse(Long id);

	public List<PhotoMerchant> findByUserIdAndDeletedFalse(Long id);
	

    @Query("SELECT pm.type, pm FROM PhotoMerchant pm WHERE pm.merchant.id = :merchantId AND pm.user.id = :userId")
    List<Object[]> findByMerchantIdAndUserId_(@Param("merchantId") Long merchantId, @Param("userId") Long userId);
}
