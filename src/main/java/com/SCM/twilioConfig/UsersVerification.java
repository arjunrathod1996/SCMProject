package com.SCM.twilioConfig;

import java.util.Date;

import com.SCM.entities.User;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Table(name = "user_verification")
@Entity
public class UsersVerification extends BigBaseEntity{
	
	public static enum Status {
		ACTIVE , CLOSED;
	}
	
	public static enum ContentType {
		MOBILE_NUMBER , EMAIL, PASSWORD;
	}
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	User user;
	
	@Enumerated(EnumType.ORDINAL)
	ContentType contentType = ContentType.MOBILE_NUMBER;
	
	String content;
	
	String verficationCode;
	
	@Enumerated(EnumType.ORDINAL)
	Status status = Status.ACTIVE;
	
	@PrePersist
	protected void onCreate() {
		Date now = Utils.now();
		if(this.getCreationTime() == null)
			this.setCreationTime(now);
		if(this.getUpdateTime() == null)
			this.setUpdateTime(now);
	}
	
	@PreUpdate
	protected void onUpdate() {
		
		this.setUpdateTime(Utils.now());	
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getVerficationCode() {
		return verficationCode;
	}

	public void setVerficationCode(String verficationCode) {
		this.verficationCode = verficationCode;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}
