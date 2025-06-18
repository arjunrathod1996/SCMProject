package com.SCM.leads;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadContactRepository extends CrudRepository<LeadContact, Long>{

public List<LeadContact> findByLeadMainAndFirstNameContaining(LeadMain leadMain , String name);
	
	public List<LeadContact> findByLeadMainAndPhoneNumberAndDeleted(LeadMain leadMain,String phoneNumber,boolean deleted);
	
	public Integer countByLeadMainAndDeleted(LeadMain leadMain,boolean deleted);
	
	@Query(value = "SELECT l.LEAD_ID , COALESCE(COUNT(l.ID), 0) "
			+ "FROM LEAD_CONTACT l "
			+ "WHERE l.BUSINESS_ID =:businessID "
			+ "AND DELETED = false "
			+ "AND l.LEAD_ID IN :leadIDs "
			+ "GROUP BY l.LEAD_ID ",nativeQuery = true)
	public List<Object[]> countByLeadMain(@Param("businessID") Long businessID,@Param("leadIDs") List<Long> leadIDs);
	
	@Query(value = "SELECT l FROM LeadContact l "
			+ "WHERE l.deleted = false "
			+ "AND l.business.id = :businessID "
			+ "AND l.id = :contactID "
			+ "AND(:assigneeID IS NULL OR (:assigneeID IS NOT NULL AND l.leadMain.assignedTo.id = :assigneeID))")
	public LeadContact findByBusinessAndAsignee(@Param("contactID") Long contactID,
			@Param("businessID") Long businessID,
			@Param("assigneeID") Long assigneeID);
	
	@Query(value = "SELECT l FROM LeadContact l "
			+ "WHERE l.deleted = false "
			+ "AND l.business.id = :businessID "
			+ "AND (:assigneeID IS NULL OR (:assigneeID IS NOT NULL AND l.leadMain.assignedTo.id = :assigneeID)) "
			+ "AND (:name IS NULL OR LOWER(l.firstName) LIKE CONCAT('%',LOWER(:name),'%') OR LOWER(l.lastName) LIKE CONCAT('%',LOWER(:name),'%')) "
			+ "AND (:email IS NULL OR LOWER(l.email) LIKE CONCAT('%',LOWER(:email),'%')) "
			+ "AND (:phoneNumber IS NULL OR l.phoneNumber LIKE CONCAT('%', :phoneNumber, '%') OR l.phoneNumber2 LIKE CONCAT('%', :phoneNumber, '%')) "
			+ "AND (:designation IS NULL OR LOWER(l.designation) LIKE CONCAT('%',LOWER(:designation),'%')) "
			+ "AND (:leadName IS NULL OR LOWER(l.leadMain.name) LIKE CONCAT('%',LOWER(:leadName),'%')) "
			+ "AND (:leadID IS NULL OR l.leadMain.leadID = :leadID) "
			+ "AND (:creator IS NULL OR l.createdBy.email = :creator) "
			+ "AND (:important IS NULL OR l.important = :important) "
			+ "AND ( (:startDate IS NULL AND :endDate IS NULL ) OR (DATE(l.creationTime) "
			+ "BETWEEN DATE(STR_TO_DATE(:startDate,'%Y-%m-%d') ) AND DATE(STR_TO_DATE(:endDate,'%Y-%m-%d') ) ) ) ")
	public Page<LeadContact> findLeadContactPageWise(Pageable pageable,
			@Param("businessID") Long businessID,
			@Param("assigneeID") Long assigneeID,
			@Param("name") String name,
			@Param("email") String email,
			@Param("phoneNumber") String phoneNumber,
			@Param("designation") String designation,
			@Param("leadName") String leadName,
			@Param("leadID") String leadID,
			@Param("creator") String creator,
			@Param("important") Boolean important,
			@Param("startDate") String startDate,
			@Param("endDate") String endDate
			);
	
	@Query(value = "SELECT LEAD_ID, COUNT(ID) FROM LEAD_CONTACT "
			+ "WHERE LEAD_ID IN :leadIDs "
			+ "GROUP BY LEAD_ID ", nativeQuery = true)
	public List<Object[]> findContactCount(@Param("leadIDs") List<Long> leadIDs);
	
	@Query(value = "SELECT LEAD_ID, COUNT(ID), MAX(CREATION_TIME) FROM LEAD_ACTIVITY "
			+ "WHERE DELETED = 0 "
			+ "AND LEAD_ID IN :leadIDs "
			+ "GROUP BY LEAD_ID ", nativeQuery = true)
	public List<Object[]> findCuntAndLastActivityByLead(@Param("leadIDs") List<Long> leadIDs);
} 

