package com.SCM.twilioConfig;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.entities.User;
import com.SCM.twilioConfig.UsersVerification.ContentType;
import com.SCM.twilioConfig.UsersVerification.Status;

@Repository
public interface UsersVerificationRepository extends CrudRepository<UsersVerification, Long>{

	Optional<UsersVerification> findByContentAndContentType(String mobileNumber, ContentType mobileNumber2);
	
	 UsersVerification findByContentAndVerficationCodeAndStatus(String content, String verficationCode, UsersVerification.Status status);

	UsersVerification findByContentAndStatus(String mobileNumber, Status active);

	
	 @Query(value = "SELECT * FROM user_verification AS u WHERE u.content = :mobileNumber AND u.status = :status AND u.user_id = :userId", nativeQuery = true)
	 UsersVerification findByUserPhoneNumberAndStatus(@Param("mobileNumber") String mobileNumber, @Param("status") Status active, @Param("userId") Long userId);

}
