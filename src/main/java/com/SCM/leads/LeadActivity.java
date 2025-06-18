package com.SCM.leads;

import java.util.Date;

import com.SCM.entities.User;
import com.SCM.leads.LeadMain.Status;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "lead_activity")
public class LeadActivity extends BigBaseEntity{
	
	public static enum ActivityType{
		
		ASSIGNED("Assigneed"),
		TASK("Task"),
		STATUS_CHANGED("Status Chaned"),
		TASK_CALL("Call"),
		TASK_EMAIL("Email"),
		TASK_MEETING("Meeting");
		
		String tag;

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		private ActivityType(String tag) {
			this.tag = tag;
		}
		
	}
	
	public static enum ActivityStatis{
		
		Open,Complete,Cancel;
		
	}
	
	@ManyToOne
	@JoinColumn(name = "lead_id")
	LeadMain leadMain;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "lead_contact_id")
	LeadContact contact;
	
	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)
	Status status;
	
	@Column(name = "activity_type")
	@Enumerated(EnumType.ORDINAL)
	ActivityType activityType;
	
	@Column(name = "activity_status")
	@Enumerated(EnumType.ORDINAL)
	ActivityStatis activityStatus;
	
	@ManyToOne
	@JoinColumn(name = "assigned_to")
	User assignedTo;
	
	@ManyToOne
	@JoinColumn(name = "created_by")
	User createdBy;
	
	@Column(name = "message")
	String message;
	
	@Column(name = "deleted")
	boolean deleted;
	
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

	public LeadMain getLeadMain() {
		return leadMain;
	}

	public void setLeadMain(LeadMain leadMain) {
		this.leadMain = leadMain;
	}

	public LeadContact getContact() {
		return contact;
	}

	public void setContact(LeadContact contact) {
		this.contact = contact;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public ActivityStatis getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(ActivityStatis activityStatus) {
		this.activityStatus = activityStatus;
	}

	public User getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	
}

