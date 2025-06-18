package com.SCM.leads;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.leads.LeadMain.Priority;
import com.SCM.leads.LeadMain.Status;

@Repository
public interface LeadMainRepository extends CrudRepository<LeadMain, Long>{

	public Integer countByCreatedByAndDeleted(User createdBy, boolean deleted);
	
	public Integer countByCreatedByAndAssignedToIsNullAndDeleted(User user,boolean deleted);

	public Integer countByCreatedByAndAssignedToInAndStatusInAndDeleted(User createdBy,List<User> assignedTo,List<Status> status,boolean deleted);

	public List<LeadMain> findByLeadIDAndDeleted(String leadID,boolean deleted);
	
	public List<LeadMain> findAllByBusinessAndDeleted(Business business,boolean deleted);
	
	public List<LeadMain> findAllByAssignedToAndDeleted(User user,boolean deleted);
	
	public List<LeadMain> findAllByCreatedByAndDeleted(User user, boolean deleted);
	
	public Integer countByBusinessAndDeleted(Business business, boolean deleted);
	
	public List<LeadMain> findByBusinessAndDeletedAndNameContaining(Business business,boolean deleted,String name);

	public List<LeadMain> findByBusinessAndDeletedAndAssignedToAndNameContaining(Business business,boolean deleted,User assignedTo,String name);
	
	@Query(value = "SELECT STATUS , COALESCE(COUNT(ID),0) FROM LEAD_MAIN "
			+ "WHERE DELETED = FALSE "
			+ "AND BUSINESS_ID = ?1 "
			+ "GROUP BY STATUS ",nativeQuery = true)
	public List<Object[]> countByBusinessAndStatus(Long businessID);

	@Query(value = "SELECT STATUS,COALESCE(COUNT(ID),0) FROM LEAD_MAIN "
			+ "WHERE DELETED = FALSE "
			+ "AND ASSIGNED_TO IS NOT NULL "
			+ "AND ASSIGNED_TO = ?1 "
			+ "GROUP BY STATUS ",nativeQuery = true)
	public List<Object[]> countByAssignedToAndStatus(Long assigneeID);
	
	@Query(value = "SELECT COALESCE(COUNT(ID),0) FROM LEAD_MAIN "
			+ "WHERE DELETED = FALSE "
			+ "AND ASSIGNED_TO IS NOT NULL "
			+ "AND ASSIGNED_TO = ?1 ",nativeQuery = true)
	public Integer countByAssignedTo(Long assigneeID);
	
	@Query(value = "SELECT ASSIGNED_TO , STATUS, COUNT(STATUS) FROM LEAD_MAIN "
			+ "WHERE DELETED = FALSE "
			+ "AND ASSIGNED_TO IS NOT NULL "
			+ "AND ASSIGNED_TO IN ?1 "
			+ "GROUP BY STATUS,ASSIGNED_TO ",nativeQuery = true)
	public List<Object[]> findByAssignee(List<Long> assigneeIDs);
	
	@Query(value = "SELECT ASSIGNED_TO , COALESCE(COUNT(ID),0) assignmentCount FROM LEAD_MAIN "
			+ "WHERE DELETED = FALSE "
			+ "AND ASSIGNED_TO IS NOT NULL "
			+ "AND ASSIGNED_TO IN :assignedTo "
			+ "GROUP BY ASSIGNED_TO "
			+ "ORDER BY assignmentCount ASC ",nativeQuery = true)
	public List<Object[]> countByAssignedTos(@Param("assignedTo")List<Long> assignedTos);

	@Query(value = "SELECT COALESCE(COUNT(ID), 0) FROM LEAD_MAIN "
			+ "WHERE BUSINESS_ID = :businessID "
			+ "AND NEXT_FOLLOWUP_TIME IS NOT NULL "
			+ "AND Date(CONVERT_TZ(NEXT_FOLLOWUP_TIME, :utcOffSet, :istOffSet)) = DATE(:time)" ,nativeQuery = true)
	public Integer countOfNextFollowUpsBusiness(@Param("businessID") Long businessID,
			@Param("time") Date followUpDate,
			@Param("utcOffSet") String utcOffSet,
			@Param("istOffSet") String istOffSet);


	@Query(value = "SELECT COALESCE(COUNT(ID), 0) FROM LEAD_MAIN "
			+ "WHERE ASSIGNED_TO IS NOT NULL "
			+ "AND ASSIGNED_TO = :assigneeID "
			+ "AND NEXT_FOLLOWUP_TIME IS NOT NULL "
			+ "AND Date(CONVERT_TZ(NEXT_FOLLOWUP_TIME, :utcOffSet, :istOffSet)) = DATE(:time)" ,nativeQuery = true)
	public Integer countOfNextFollowUpsByAssignedTo(@Param("assigneeID") Long assigneeID,
			@Param("time") Date followUpDate,
			@Param("utcOffSet") String utcOffSet,
			@Param("istOffSet") String istOffSet);
	
	@Query(value = "SELECT * FROM LEAD_MAIN "
			+ "WHERE DELETED = FALSE "
			+ "AND CREATED_BY = :createdBy "
			+ "AND STATUS IN :status "
			+ "AND ASSIGNED_TO IS NOT NULL "
			+ "AND ASSIGNED_TO = :assignedTo "
			+ "ORDER BY CREATION_TIME DESC",nativeQuery = true)
	public List<LeadMain> findByCreatedByAndStatusAndAssignedTo(@Param("createdBy") Long createdBy,
			@Param("status") List<Integer> status,
			@Param("assignedTo") Long assignedTo);
	
	@Query(value = "SELECT * FROM LEAD_MAIN "
			+ "WHERE DELETED = FALSE "
			+ "AND CREATED_BY = :createdBy "
			+ "AND STATUS IN :status "
			+ "ORDER BY CREATION_TIME DESC",nativeQuery = true)
	public List<LeadMain> findByCreatedByAndStatus(@Param("createdBy") Long createdBy,
			@Param("status") List<Integer> status);
	
	@Query(value = "SELECT * FROM LEAD_MAIN "
			+ "WHERE DELETED = FALSE "
			+ "AND CREATED_BY = :createdBy "
			+ "AND STATUS IN :status "
			+ "AND ASSIGNED_TO IS NOT NULL "
			+ "AND ASSIGNED_TO = :assignedTo "
			+ "AND NEXT_FOLLOWUP_TIME IS NOT NULL "
			+ "AND Date(CONVERT_TZ(NEXT_FOLLOWUP_TIME, :utcOffSet, :istOffSet)) "
			+ "BETWEEN DATE(STR_TO_DATE(:startDate,'%Y-%m-%d'))"
			+ "AND DATE(STR_TO_DATE(:endDate, '%Y-%m-%d')) "
			+ "ORDER BY CREATION_TIME DESC",nativeQuery = true)
	public List<LeadMain> findByCreatedByAndStatusAndAssignedToAndFollowUpTime(@Param("createdBy") Long createdBy,
			@Param("status") List<Integer> status,
			@Param("assignedTo") Long assignedTo,
			@Param("startDate") String startDate,
			@Param("endDate") String endDate,
			@Param("utcOffSet") String utcOffSet,
			@Param("istOffSet") String istOffSet);
	
	@Query(value = "SELECT DISTINCT SOURCE FROM LEAD_MAIN "
            + "WHERE BUSINESS_ID = ?1 "
            + "AND SOURCE IS NOT NULL "
            + "AND SOURCE != ''", nativeQuery = true)
    public List<String> findSourceByBusiness(Long businessID);
	
	@Query(value = "SELECT DISTINCT CATEGORY FROM LEAD_MAIN "
            + "WHERE BUSINESS_ID = ?1 "
            + "AND CATEGORY IS NOT NULL "
            + "AND CATEGORY != ''", nativeQuery = true)
    public List<String> findCategoryBusiness(Long businessID);
	
	
	@Query(value = "SELECT DISTINCT SOURCE,COUNT(ID) AS COUNT  FROM LEAD_MAIN "
            + "WHERE DELETED = FALSE "
            + "AND SOURCE IS NOT NULL "
            + "AND SOURCE != '' "
            + "AND BUSINESS_ID = ?1 "
            + "GROUP BY SOURCE "
            + "ORDER BY COUNT DESC "
            + "LIMIT ?2 ", nativeQuery = true)
	public List<Object[]> findTopSourceByBusiness(Long businessID,Integer limit);
	
//	@Query(value = "SELECT l FROM LeadMain l " +
//	        "WHERE l.deleted = false " +
//	        "AND l.business.id = :businessID " +
//	        "AND (:asigneeID IS NULL OR l.assignedTo.id = :asigneeID) " +
//	        "AND (:status IS NULL OR l.status = :status) " +
//	        "AND (:important IS NULL OR l.important = :important) " +
//	        "AND (:priority IS NULL OR l.priority = :priority) " +
//	        "AND (:leadName IS NULL OR LOWER(l.name) LIKE CONCAT('%', LOWER(:leadName), '%')) " +
//	        "AND (:leadID IS NULL OR l.leadID = :leadID) " +
//	        "AND ( (:followUpStartDate IS NULL AND :followUpEndDate IS NULL ) OR (DATE(l.nextFollowUpTime) " +
//	        "BETWEEN DATE(STR_TO_DATE(:followUpStartDate, '%Y-%m-%d')) AND DATE(STR_TO_DATE(:followUpEndDate, '%Y-%m-%d') ) ) ) " +
//	        "AND ( (:startDate IS NULL AND :endDate IS NULL) OR (DATE(l.creationTime) " +
//	        "BETWEEN DATE(STR_TO_DATE(:startDate, '%Y-%m-%d')) AND DATE(STR_TO_DATE(:endDate, '%Y-%m-%d') ) ) ) ")
//	public Page<LeadMain> findLeadsPageWise(
//	        Pageable pageable,
//	        @Param("businessID") Long businessID,
//	        @Param("asigneeID") Long asigneeID,
//	        @Param("status") Status status,
//	        @Param("priority") Priority priority,
//	        @Param("important") Boolean important,
//	        @Param("leadName") String leadName,
//	        @Param("leadID") String leadID,
//	        @Param("followUpStartDate") String followUpStartDate,
//	        @Param("followUpEndDate") String followUpEndDate,
//	        @Param("startDate") String startDate,
//	        @Param("endDate") String endDate);
	
	
	@Query(value = "SELECT l FROM LeadMain l " +
		    "WHERE l.deleted = false " +
		    "AND l.business.id = :businessID " +
		    "AND (:creatorId IS NULL OR l.createdBy.id = :creatorId) " +
		    "AND (:assigneeID IS NULL OR l.assignedTo.id = :assigneeID) " +
		    "AND (:status IS NULL OR l.status = :status) " +
		    "AND (:important IS NULL OR l.important = :important) " +
		    "AND (:priority IS NULL OR l.priority = :priority) " +
		    "AND (:leadName IS NULL OR LOWER(l.name) LIKE CONCAT('%', LOWER(:leadName), '%')) " +
		    "AND (:leadID IS NULL OR l.leadID = :leadID) " +
		    "AND ( (:followUpStartDate IS NULL AND :followUpEndDate IS NULL ) OR (DATE(l.nextFollowUpTime) " +
		    "BETWEEN DATE(STR_TO_DATE(:followUpStartDate, '%Y-%m-%d')) AND DATE(STR_TO_DATE(:followUpEndDate, '%Y-%m-%d') ) ) ) " +
		    "AND ( (:startDate IS NULL AND :endDate IS NULL) OR (DATE(l.creationTime) " +
		    "BETWEEN DATE(STR_TO_DATE(:startDate, '%Y-%m-%d')) AND DATE(STR_TO_DATE(:endDate, '%Y-%m-%d') ) ) ) ")
		public Page<LeadMain> findLeadsPageWise(
		    Pageable pageable,
		    @Param("businessID") Long businessID,
		    @Param("creatorId") Long creatorId, // Add this parameter
		    @Param("assigneeID") Long assigneeID,
		    @Param("status") Status status,
		    @Param("priority") Priority priority,
		    @Param("important") Boolean important,
		    @Param("leadName") String leadName,
		    @Param("leadID") String leadID,
		    @Param("followUpStartDate") String followUpStartDate,
		    @Param("followUpEndDate") String followUpEndDate,
		    @Param("startDate") String startDate,
		    @Param("endDate") String endDate);

	
	
	// Repository method
//	@Query("SELECT l FROM LeadMain l WHERE l.business.id = :businessId " +
//	       "AND (:assigneeId IS NULL OR l.assignedTo.id = :assigneeId) " +
//	       "AND (:creatorId IS NULL OR l.createdBy.id = :creatorId) " + // Add this line
//	       "AND (:status IS NULL OR l.status = :status) " +
//	       "AND (:priority IS NULL OR l.priority = :priority) " +
//	       "AND (:important IS NULL OR l.important = :important) " +
//	       "AND (:leadName IS NULL OR l.name LIKE %:leadName%) " +
//	       "AND (:leadID IS NULL OR l.id = :leadID) " +
//	       "AND (:followUpStartDate IS NULL OR l.followUpDate >= :followUpStartDate) " +
//	       "AND (:followUpEndDate IS NULL OR l.followUpDate <= :followUpEndDate) " +
//	       "AND (:startDate IS NULL OR l.creationTime >= :startDate) " +
//	       "AND (:endDate IS NULL OR l.creationTime <= :endDate)")
//	Page<LeadMain> findLeadsPageWise(Pageable pageable,
//	                                 @Param("businessId") Long businessId,
//	                                 @Param("assigneeId") Long assigneeId,
//	                                 @Param("creatorId") Long creatorId, // Add this parameter
//	                                 @Param("status") Status status,
//	                                 @Param("priority") Priority priority,
//	                                 @Param("important") Boolean important,
//	                                 @Param("leadName") String leadName,
//	                                 @Param("leadID") String leadID,
//	                                 @Param("followUpStartDate") String followUpStartDate,
//	                                 @Param("followUpEndDate") String followUpEndDate,
//	                                 @Param("startDate") String startDate,
//	                                 @Param("endDate") String endDate);

	
	
	public List<LeadMain> findAll();
	
	@Query("SELECT COUNT(lm) FROM LeadMain lm WHERE lm.createdBy = :user")
    Integer countByCreatedBy(User user);

	public int countByBusiness(Business business);


}
