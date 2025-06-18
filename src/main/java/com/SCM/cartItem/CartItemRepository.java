package com.SCM.cartItem;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.cartItem.CartItem.CartItemStatus;
import com.SCM.controllers.Merchant;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.leads.LeadMain;
import com.SCM.leads.LeadMain.Priority;
import com.SCM.leads.LeadMain.Status;
import com.SCM.photo.PhotoMerchant;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, Long>{

	Optional<CartItem> findByCustomerAndPhotoMerchant(Customer customer, PhotoMerchant photoMerchant);

	List<CartItem> findByCustomer(Customer customer);

	 @Query("SELECT SUM(c.quantity) AS totalQuantity FROM CartItem c JOIN c.photoMerchant p " +
	           "WHERE c.status = :status AND p.user.id = :userId AND p.merchant.id = :merchantId " +
	           "AND c.customer.id = :customerId "
	           + "AND c.deleted = false ")
	    Integer getTotalQuantityByUserIdAndMerchantIdAndCustomerId(
	        @Param("status") CartItem.CartItemStatus status,
	        @Param("userId") Long userId,
	        @Param("merchantId") Long merchantId,
	        @Param("customerId") Long customerId);
	 
	 
	 @Query("SELECT SUM(c.amount) AS totalAmount FROM CartItem c JOIN c.photoMerchant p " +
	           "WHERE c.status = :status AND p.user.id = :userId AND p.merchant.id = :merchantId " +
	           "AND c.customer.id = :customerId "
	           + "AND c.deleted = false ")
	    Integer getTotalAmountByUserIdAndMerchantIdAndCustomerId(
	        @Param("status") CartItem.CartItemStatus status,
	        @Param("userId") Long userId,
	        @Param("merchantId") Long merchantId,
	        @Param("customerId") Long customerId);

	 
	 @Query("SELECT c FROM CartItem c JOIN c.photoMerchant p " +
		       "WHERE c.status = :status AND p.user.id = :userId AND p.merchant.id = :merchantId " +
		       "AND c.customer.id = :customerId "
		       + "AND c.deleted = false ")
		List<CartItem> getCartItemByUserIdAndMerchantIdAndCustomerId(@Param("status") CartItemStatus status, 
		                                                             @Param("userId") Long userId, 
		                                                             @Param("merchantId") Long merchantId, 
		                                                             @Param("customerId") Long customerId);
	 
	 @Query("SELECT c FROM CartItem c JOIN c.photoMerchant p " +
		       "WHERE c.status = :status AND p.user.id = :userId AND p.merchant.id = :merchantId " +
		       "AND c.customer.id = :customerId "
		       + "AND p.id = :photoId "
		       + "AND p.deleted = false ")
		List<CartItem> getCartItemByUserIdAndMerchantIdAndCustomerIdAndPhotoMerchantId(@Param("status") CartItemStatus status, 
		                                                             @Param("userId") Long userId, 
		                                                             @Param("merchantId") Long merchantId, 
		                                                             @Param("customerId") Long customerId,
		                                                             @Param("photoId") Long photoId);
	 
//	 @Query("SELECT c FROM CartItem c JOIN c.photoMerchant p " +
//		       "WHERE p.user.id = :userId AND p.merchant.id = :merchantId " +
//		       "AND c.customer.id = :customerId "
//		       + "AND p.id = :photoId")
//		List<CartItem> getCartItemByUserIdAndMerchantIdAndCustomerIdAndPhotoMerchantId(
//		                                                             @Param("userId") Long userId, 
//		                                                             @Param("merchantId") Long merchantId, 
//		                                                             @Param("customerId") Long customerId,
//		                                                             @Param("photoId") Long photoId);
	 
	 @Query(value = "SELECT c.* FROM CART_ITEM AS c "
	 		+ "JOIN PHOTO_MERCHANT AS p "
	 		+ "ON c.PHOTO_MERCHANT_ID = p.id "
	 		+ "WHERE c.STATUS = :status "
	 		+ "AND c.CUSTOMER_ID = :customerId "
	 		+ "AND p.MERCHANT_ID = :merchantId "
	 		+ "AND p.USER_ID = :userId "
	 		+ "AND c.PHOTO_MERCHANT_ID = :photoId",nativeQuery = true)
	 CartItem getCartItemByUserIdAndMerchantIdAndCustomerIdAndPhotoMerchantIds(@Param("status") CartItemStatus status, 
		                                                             @Param("userId") Long userId, 
		                                                             @Param("merchantId") Long merchantId, 
		                                                             @Param("customerId") Long customerId,
		                                                             @Param("photoId") Long photoId);

	 List<CartItem> findByCustomerAndStatus(Customer customer, CartItemStatus status);

	Optional<CartItem> findFirstByCustomerAndPhotoMerchantOrderByCreationTimeDesc(Customer customer, PhotoMerchant photoMerchant);

//	List<CartItem> findByCustomerAndPhotoMerchantAndStatus(Customer customer, PhotoMerchant photoMerchant,
//			CartItemStatus pending);
	
	 @Query("SELECT ci FROM CartItem ci WHERE ci.customer = :customer AND ci.photoMerchant = :photoMerchant AND ci.status = :status AND ci.deleted = false")
	    List<CartItem> findByCustomerAndPhotoMerchantAndStatus(@Param("customer") Customer customer,
	                                                           @Param("photoMerchant") PhotoMerchant photoMerchant,
	                                                           @Param("status") CartItem.CartItemStatus status);
	
	
	@Query(value = "SELECT AVG(c.rating) FROM Cart_Item c "
            + "JOIN Photo_Merchant p ON p.id = c.photo_merchant_id "
            + "WHERE c.rating IS NOT NULL " 
            + "AND p.merchant_id = :merchantId "
            + "AND p.user_id = :userId "
            + "AND c.status = :status "
            + "AND c.deleted = false ", nativeQuery = true)
Double findAverageRatingByMerchantIdAndUserId(@Param("status") CartItem.CartItemStatus status,
                                             @Param("merchantId") Long merchantId,
                                             @Param("userId") Long userId);
	
	
	 @Query("SELECT c FROM CartItem c JOIN c.photoMerchant p " +
	           "WHERE p.merchant.id = :merchantId AND p.user.id = :userId "
	           + "AND c.deleted = false ")
	    List<CartItem> findAllByMerchantIdAndUserId(@Param("merchantId") Long merchantId, @Param("userId") Long userId);
	 
	 @Query("SELECT c FROM CartItem c JOIN c.photoMerchant p " +
	           "WHERE c.status IN :statuses " +
	           "AND p.user.id = :userId " +
	           "AND p.merchant.id = :merchantId " +
	           "AND c.customer.id = :customerId " +
	           "AND p.id = :photoId "
	           + "AND c.deleted = false ")
	    List<CartItem> findByStatusesAndUserIdAndMerchantIdAndCustomerIdAndPhotoMerchantId(
	            @Param("statuses") List<CartItemStatus> statuses,
	            @Param("userId") Long userId,
	            @Param("merchantId") Long merchantId,
	            @Param("customerId") Long customerId,
	            @Param("photoId") Long photoId);
	 
	 
	 @Query("SELECT AVG(c.rating) FROM CartItem c " +
		       "JOIN c.photoMerchant p " +
		       "WHERE c.rating IS NOT NULL " +
		       "AND p.merchant.id = :merchantId " +
		       "AND p.user.id = :userId " +
		       "AND c.status = :status " +
		       "AND p.id = :photoId "
		       + "AND c.deleted = false ")
		Double findAverageRatingByMerchantIdAndUserIdAndPhotoId(@Param("status") CartItemStatus status,
		                                                       @Param("merchantId") Long merchantId,
		                                                       @Param("userId") Long userId,
		                                                       @Param("photoId") Long photoId);

	 @Query(value = "SELECT c.photoMerchant.id, AVG(c.rating) FROM CartItem c " +
             "WHERE c.rating IS NOT NULL "
             + "and c.deleted = false " +
             "GROUP BY c.photoMerchant.id ")
List<Object[]> findAverageRatingByPhotoMerchantId();

@Query(value = "select c from CartItem c "
		+ "Where c.photoMerchant.merchant = :merchant "
		+ "and c.photoMerchant.user = :user "
		+ "and c.customer = :customer "
		+ "and c = :cartItem ")
Optional<CartItem> findByMerchantAndUserAndCustomer(
		@Param("cartItem") CartItem cartItem,
        @Param("merchant") Merchant merchant,
        @Param("user") User user,
        @Param("customer") Customer customer
);

@Query("SELECT c FROM CartItem c " +
        "WHERE c.photoMerchant.merchant = :merchant " +
        "AND c.photoMerchant.user = :user " +
        "AND c.customer = :customer " +
        "AND c.id = :itemId")
 Optional<CartItem> findByMerchantAndUserAndCustomer(
     @Param("merchant") Merchant merchant,
     @Param("user") User user,
     @Param("customer") Customer customer,
     @Param("itemId") Long itemId
 );

@Query("SELECT " +
        "COUNT(c.id), " +
        "SUM(CASE WHEN c.status = com.SCM.cartItem.CartItem$CartItemStatus.COMPLETED THEN 1 ELSE 0 END), " +
        "SUM(CASE WHEN c.status = com.SCM.cartItem.CartItem$CartItemStatus.PENDING THEN 1 ELSE 0 END), " +
        "SUM(CASE WHEN c.status = com.SCM.cartItem.CartItem$CartItemStatus.CANCELED THEN 1 ELSE 0 END), " +
        "SUM(CASE WHEN c.status = com.SCM.cartItem.CartItem$CartItemStatus.COMPLETED THEN c.amount ELSE 0 END), " +
        "SUM(CASE WHEN c.status = com.SCM.cartItem.CartItem$CartItemStatus.PENDING THEN c.amount ELSE 0 END), " +
        "SUM(CASE WHEN c.status = com.SCM.cartItem.CartItem$CartItemStatus.CANCELED THEN c.amount ELSE 0 END) " +
        "FROM CartItem c " +
        "JOIN c.photoMerchant p " +
        "WHERE p.merchant.id IN :merchantIds")
 List<Object[]> getCartItemStatistics(@Param("merchantIds") List<Long> merchantIds);
 
 
 @Query(value = "SELECT " +
         "DATE_FORMAT(r.CREATION_TIME, '%b,%Y') AS CR_DATE, " +
         "COUNT(r.ID) AS COUNT_CART, " +
         "COALESCE(SUM(r.AMOUNT), 0) AS R_AMOUNT " +
         "FROM CART_ITEM r " +
         "JOIN PHOTO_MERCHANT AS p " +
         "ON p.id = r.photo_merchant_id " +
         "WHERE r.STATUS = 1 " +
         "AND p.MERCHANT_ID IN (:merchantIDs) " +
         "AND TIMESTAMPDIFF(DAY, DATE_FORMAT(r.CREATION_TIME, '%Y-%m-%d'), " +
         "DATE_FORMAT(UTC_TIMESTAMP(), '%Y-%m-%d')) <= :duration " +
         "GROUP BY CR_DATE " +
         "ORDER BY CR_DATE DESC", nativeQuery = true)
 List<Object[]> getCombinedData(@Param("merchantIDs") List<Long> merchantIDs, @Param("duration") Integer duration);
 
 
// @Query(value = "SELECT c.CUSTOMER_ID, COALESCE(COUNT(c.id),0), COALESCE(SUM(c.AMOUNT),0) " +
//         "FROM CART_ITEM AS c " +
//         "JOIN PHOTO_MERCHANT AS p ON p.ID = c.PHOTO_MERCHANT_ID " +
//         "WHERE c.STATUS = 1 " +
//         "AND p.MERCHANT_ID IN :merchantIds " +
//         "AND c.CUSTOMER_ID IS NOT NULL " +
//         "AND c.DELETED = false " +
//         "AND (DATE(c.creationTime) BETWEEN DATE(STR_TO_DATE(:startDate, '%Y-%m-%d')) " +
//         "AND DATE(STR_TO_DATE(:endDate, '%Y-%m-%d'))) " +
//         "GROUP BY c.CUSTOMER_ID " +
//         "ORDER BY COALESCE(COUNT(c.id),0) DESC", nativeQuery = true)
//List<Object[]> findCustomerCartItemCounts(@Param("merchantIds") List<Long> merchantIds,
//                                    @Param("startDate") String startDate,
//                                    @Param("endDate") String endDate);
 
// @Query("SELECT c.customer.id, COALESCE(COUNT(c.id), 0), COALESCE(SUM(c.amount), 0) " +
//         "FROM CartItem AS c " +
//         "JOIN PhotoMerchant AS p ON p.id = c.photoMerchant.id " +
//         "WHERE c.status = 1 " +
//         "AND p.merchant.id IN :merchantIds " +
//         "AND c.customer.id IS NOT NULL " +
//         "AND c.deleted = false " +
//         "AND (DATE(c.creationTime) BETWEEN DATE(STR_TO_DATE(:startDate,'%Y-%m-%d') ) " +
//		 "AND DATE(STR_TO_DATE(:endDate,'%Y-%m-%d'))) " +
//         "GROUP BY c.customer.id " +
//         "ORDER BY COALESCE(COUNT(c.id), 0) DESC")
//List<Object[]> findCustomerCartItemCounts(@Param("merchantIds") List<Long> merchantIds,
//                                    @Param("startDate") String startDate,
//                                    @Param("endDate") String endDate, Pageable page);
 
 @Query("SELECT c.customer.id, COALESCE(COUNT(c.id), 0), COALESCE(SUM(c.amount), 0) " +
         "FROM CartItem AS c " +
         "JOIN PhotoMerchant AS p ON p.id = c.photoMerchant.id " +
         "WHERE c.status = 1 " +
         "AND p.merchant.id IN :merchantIds " +
         "AND c.customer.id IS NOT NULL " +
         "AND c.deleted = false " +
         "AND (DATE(c.creationTime) BETWEEN DATE(STR_TO_DATE(:startDate,'%Y-%m-%d')) " +
         "AND DATE(STR_TO_DATE(:endDate,'%Y-%m-%d'))) " +
         "GROUP BY c.customer.id " +
         "ORDER BY COALESCE(COUNT(c.id), 0) DESC")
  List<Object[]> findCustomerCartItemCounts(@Param("merchantIds") List<Long> merchantIds,
                                            @Param("startDate") String startDate,
                                            @Param("endDate") String endDate,
                                            Pageable page);
  
  
  @Query(value = "SELECT c FROM CartItem c "
  		+ "Where c.photoMerchant.merchant.business.id = :businessID "
  		+ "and (:merchantID is null or c.photoMerchant.merchant.id = :merchantID) "
  		+ "and (:email is null or c.customer.email = :email) "
  		+ "and (:mobileNumber is null or c.customer.mobileNumber = :mobileNumber) "
  		+ "AND (:status is null or c.status = :status)"
  		+ "AND ( (:startDate IS NULL AND :endDate IS NULL) OR (DATE(c.creationTime) "
		+"BETWEEN DATE(STR_TO_DATE(:startDate, '%Y-%m-%d')) AND DATE(STR_TO_DATE(:endDate, '%Y-%m-%d') ) ) ) ")
		public Page<CartItem> findCartItemPageWise(
		    Pageable pageable,
		    @Param("businessID") Long businessID,
		    @Param("merchantID") Long merchantID,
		    @Param("email") String email,
		    @Param("mobileNumber") String mobileNumber,
		    @Param("startDate") String startDate,
		    @Param("endDate") String endDate,
		    @Param("status") CartItemStatus status);
  
 
//  @Query(value = "SELECT c FROM CartItem c "
//	  		+ "Where c.photoMerchant.merchant.business.id = :businessID "
//	  		+ "and c.photoMerchant.user.id = :userID "
//	  		+ "and c.photoMerchant.merchant.id = :merchantID "
//	  		+ "and (:email is null or c.customer.email = :email) "
//	  		+ "and (:mobileNumber is null or c.customer.mobileNumber = :mobileNumber) "
//	  		+ "AND (:status is null or c.status = :status)"
//	  		+ "AND ( (:startDate IS NULL AND :endDate IS NULL) OR (DATE(c.creationTime) "
//			+"BETWEEN DATE(STR_TO_DATE(:startDate, '%Y-%m-%d')) AND DATE(STR_TO_DATE(:endDate, '%Y-%m-%d') ) ) ) ")
//			public Page<CartItem> findCartItemCustomerPageWise(
//			    Pageable pageable,
//			    @Param("businessID") Long businessID,
//			    @Param("userID") Long userID,
//			    @Param("merchantID") Long merchantID,
//			    @Param("email") String email,
//			    @Param("mobileNumber") String mobileNumber,
//			    @Param("startDate") String startDate,
//			    @Param("endDate") String endDate,
//			    @Param("status") CartItemStatus status);
  
  
  @Query(value = "SELECT DISTINCT c.id, c.bithday, c.creationTime, c.email, c.firstName, c.gender, c.lastName, c.mobileNumber FROM CartItem ci "
	        + "JOIN ci.photoMerchant pm "
	        + "JOIN pm.merchant m "
	        + "JOIN ci.customer c "
	        + "WHERE m.business.id = :businessID "
	        + "AND pm.user.id = :userID "
	        + "AND pm.merchant.id = :merchantID "
	        + "AND (:email IS NULL OR c.email = :email) "
	        + "AND (:mobileNumber IS NULL OR c.mobileNumber = :mobileNumber) "
	        + "AND (:status IS NULL OR ci.status = :status) "
	        + "AND ((:startDate IS NULL AND :endDate IS NULL) OR (DATE(ci.creationTime) BETWEEN DATE(STR_TO_DATE(:startDate, '%Y-%m-%d')) AND DATE(STR_TO_DATE(:endDate, '%Y-%m-%d')))) "
	        + "ORDER BY c.id DESC")
	public Page<CartItem> findDistinctCustomers(
	        Pageable pageable,
	        @Param("businessID") Long businessID,
	        @Param("userID") Long userID,
	        @Param("merchantID") Long merchantID,
	        @Param("email") String email,
	        @Param("mobileNumber") String mobileNumber,
	        @Param("startDate") String startDate,
	        @Param("endDate") String endDate,
	        @Param("status") CartItemStatus status);




  
  @Query("SELECT c FROM CartItem c WHERE c.photoMerchant.merchant.business.id = :businessId AND " +
          "(:merchantId IS NULL OR c.photoMerchant.merchant.id = :merchantId) AND " +
          "(:email IS NULL OR c.customer.email = :email) AND " +
          "(:mobileNumber IS NULL OR c.customer.mobileNumber = :mobileNumber) AND " +
          "(:startDate IS NULL OR c.creationTime >= :startDate) AND " +
          "(:endDate IS NULL OR c.creationTime <= :endDate) AND " +
          "(:status IS NULL OR c.status = :status)")
  List<CartItem> findCartItemsWithoutPagination(@Param("businessId") Long businessId,
                                                @Param("merchantId") Long merchantId,
                                                @Param("email") String email,
                                                @Param("mobileNumber") String mobileNumber,
                                                @Param("startDate") String startDate,
                                                @Param("endDate") String endDate,
                                                @Param("status") CartItemStatus status,
                                                Sort sort); 

}


