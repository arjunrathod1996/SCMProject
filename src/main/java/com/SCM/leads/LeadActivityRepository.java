package com.SCM.leads;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.leads.LeadActivity.ActivityType;

@Repository
public interface LeadActivityRepository extends CrudRepository<LeadActivity, Long>{

	public List<LeadActivity> findAllByLeadMainInAndDeletedOrderByCreationTimeDesc(List<LeadMain> leads,
			boolean deleted);
	
	public Integer countByLeadMainAndDeleted(LeadMain lead,boolean deleted);
	
	@Query(value = "SELECT * FROM LEAD_ACTIVITY "
			+ "WHERE DELETED = FALSE "
			+ "AND LEAD_ID IN :leadIDs "
			+ "ORDER BY CREATION_TIME DESC ",nativeQuery = true)
	public List<LeadActivity> findAllByLeads(@Param("leadIDs") Long leadIDs);
	
	@Query(value = "SELECT l FROM LeadActivity l "
			+ "WHERE l.deleted = false "
			+ "AND l.leadMain.business.id = :businessID "
			+ "AND l.id = :activityID "
			+ "AND (:assigneeID IS NULL OR (:assigneeID IS NOT NULL AND l.leadMain.assignedTo.id = :assigneeID )) "
			+ "AND l.activityType IN :activityTypes ")
	public LeadActivity findByBusinessAndAssignee(@Param("activityID") Long activityID,
			@Param("businessID") Long businessID,
			@Param("assigneeID") Long assigneeID,
			@Param("activityTypes") List<ActivityType> activityTypes);
	
	@Query(value = "SELECT LEAD_ID , COUNT(ID), MAX(CREATION_TIME) FROM LEAD_ACTIVITY "
			+ "WHERE DELETED = FALSE "
			+ "AND LEAD_ID IN :leadIDs "
			+ "ORDER BY LEAD_ID ",nativeQuery = true)
	public List<Object[]> findCountAndLastActivityByLead(@Param("leadIDs") Long leadIDs);
	
	@Query(value = "SELECT l FROM LeadActivity l "
			+ "WHERE l.deleted = false "
			+ "AND l.leadMain.business.id = :businessID "
			+ "AND (:assigneeID IS NULL OR (:assigneeID IS NOT NULL AND l.leadMain.assignedTo.id = :assigneeID)) "
			+ "AND (:activityType IS NULL OR l.activityType = :activityType) "
			+ "AND (:message IS NULL OR LOWER(l.message) LIKE CONCAT('%', LOWER(:message),'%')) "
			+ "AND (:leadName IS NULL OR LOWER(l.leadMain.name) LIKE CONCAT('%',LOWER(:leadName), '%')) "
			+ "AND (:leadID IS NULL OR l.leadMain.leadID = :leadID) "
			+ "AND (:creator IS NULL OR (:creator IS NOT NULL AND l.createdBy.email = :creator)) "
			+ "AND (:contactID IS NULL OR (:contactID IS NOT NULL AND l.contact.id = :contactID)) "
			+ "AND ( (:startDate IS NULL AND :endDate IS NULL ) OR (DATE(l.creationTime) "
			+ "BETWEEN DATE(STR_TO_DATE(:startDate,'%Y-%m-%d') ) AND DATE(STR_TO_DATE(:endDate,'%Y-%m-%d') ) ) ) ")
	public Page<LeadActivity> findLeadActivityPageWise(Pageable pageable,
			@Param("businessID") Long businessID,
			@Param("assigneeID") Long assigneeID,
			@Param("activityType") ActivityType activityType,
			@Param("message") String message,
			@Param("leadName") String leadName,
			@Param("leadID") String leadID,
			@Param("creator") String creator,
			@Param("contactID") Long contactID,
			@Param("startDate") String startDate,
			@Param("endDate") String endDate);
	
	@Query(value = "SELECT LEAD_ID, COUNT(ID), MAX(CREATION_TIME) FROM LEAD_ACTIVITY "
			+ "WHERE DELETED = 0 "
			+ "AND LEAD_ID IN :leadIDs "
			+ "GROUP BY LEAD_ID ", nativeQuery = true)
	public List<Object[]> findCuntAndLastActivityByLead(@Param("leadIDs") List<Long> leadIDs);

}

