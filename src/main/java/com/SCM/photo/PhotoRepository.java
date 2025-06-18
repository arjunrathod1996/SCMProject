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
public interface PhotoRepository extends CrudRepository<Photo, Long>{
	
	public Photo findByFileNameAndDeleted(String fileName, boolean deleted);
	
	List<Photo> findByMerchantAndDeletedOrderBySequence(Merchant merchant, boolean b);

	List<Photo> findByMerchantAndTypeAndDeletedOrderBySequence(Merchant merchant, PhotoType type, boolean b);
	
	@Query(value = "SELECT p FROM Photo p "
			+ "WHERE p.deleted = false "
			+ "AND p.business.id = :businessID "
			+ "AND p.type = :type "
			+ "AND p.merchant.id IS NULL "
			+ "ORDER BY p.sequence ")
	List<Photo> findByBusinessAndType(@Param("businessID") Long businessID,@Param("type") PhotoType pwaLogoS);
	
	@Query(value = "SELECT p FROM Photo p "
			+ "WHERE p.deleted = false "
			+ "AND p.business.id = :businessID "
			+ "AND p.merchant.id IS NULL "
			+ "ORDER BY p.sequence ")
	List<Photo> findByBusinessID(@Param("businessID") Long businessID);
	
//	@Query(value = "SELECT p FROM Photo p "
//			+ "WHERE p.deleted = false "
//			+ "AND p.type = :type "
//			+ "AND p.sequence = :sequence "
//			+ "AND p.merchant.id IS NULL "
//			+ "AND p.business.id IS NULL ")
//	List<Photo> findByTypeAndSequence(@Param("type") PhotoType pwaLogoS, @Param("sequence") Long sequence);
	
//	@Query(value = "SELECT p FROM Photo p "
//			+ "WHERE p.deleted = false "
//			+ "AND p.type = :type "
//			+ "AND p.business.id = :businessID "
//			+ "AND p.merchant.id IS NULL "
//			+ "AND p.sequence = :sequence ")
//	List<Photo> findByBusinessAndTypeAndSequence(@Param("businessID") Long businessID, @Param("type") PhotoType type,
//			@Param("sequence") Long sequence);

	public Photo findByMerchantAndTypeAndSequenceAndDeleted(Merchant merchant, PhotoType type, Long sequence, boolean deleted);
	
	@Query(value = "SELECT COALESCE(COUNT(p.id),0) FROM Photo p "
			+ "WHERE p.deleted = false "
			+ "AND p.type = :photoType "
			+ "AND p.merchant.id IS NULL "
			+ "AND p.business.id IS NULL ")
	public Integer countByType(@Param("photoType") PhotoType pwaLogoS);
	
	@Query(value = "SELECT COALESCE(COUNT(p.id),0) FROM Photo p "
			+ "WHERE p.deleted = false "
			+ "AND p.type = :photoType "
			+ "AND p.business.id = :businessID "
			+ "AND p.merchant.id IS NULL ")
	public Integer countByBusiness(@Param("businessID") Long busiessID,@Param("photoType") PhotoType pwaLogoS);
	
	@Query(value = "SELECT COALESCE(COUNT(p.id),0) FROM Photo p "
			+ "WHERE p.deleted = false "
			+ "AND p.type = :photoType "
			+ "AND p.merchant.id = :merchantID ")
	public Integer countByMerchant(@Param("merchantID") Long merchantID,@Param("photoType") PhotoType pwaLogoS);
	
	@Query(value = "SELECT p FROM Photo p "
			+ "WHERE p.deleted = false "
			+ "AND p.type = :type "
			+ "AND p.sequence = :sequence "
			+ "AND p.merchant.id IS NULL "
			+ "AND p.business.id IS NULL ")
	public Photo findByTypeAndSequence(@Param("type") PhotoType pwaLogoS, @Param("sequence") Long sequence);
	
	@Query(value = "SELECT p FROM Photo p "
			+ "WHERE p.deleted = false "
			+ "AND p.type = :type "
			+ "AND p.business.id = :businessID "
			+ "AND p.merchant.id IS NULL "
			+ "AND p.sequence = :sequence ")
	public Photo findByBusinessAndTypeAndSequence(@Param("businessID") Long businessID, @Param("type") PhotoType type,
			@Param("sequence") Long sequence);

//	@Query(value = "SELECT p FROM Photo p "
//			+ "WHERE p.deleted = false "
//			+ "AND p.type = :type "
//			+ "AND p.id = :photoId "
//			+ "AND p.merchant.id IS NULL "
//			+ "ORDER BY p.sequence ")
//	public String findByPhotoIdAndType(@Param("photoId") Long photoId,@Param("type") PhotoType type);
	
	@Query("SELECT p FROM Photo p "
	           + "WHERE p.deleted = false "
	           + "AND p.type = :type "
	           + "AND p.id = :photoId "
	           + "AND p.merchant IS NULL "
	           + "ORDER BY p.sequence")
	    Optional<Photo> findByPhotoIdAndType(@Param("photoId") Long photoId, @Param("type") PhotoType customerProfile);

	public List<Photo> findByBusinessIdAndMerchantId(Long businessID, Long merchantID);

	
}
