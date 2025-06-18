package com.SCM.leads;

import java.util.Date;
import java.util.List;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "lead_main")
public class LeadMain extends BigBaseEntity{
	
	public static enum Status{
		
		Open("Open","label label-default"),
		Processing("In Process", "label label-primary"),
		Proposal("Proposal","label label-warning"),
		Close("Closure","label label-success"),
		Inavlid("Invalid","label label-danger"),
		NotIntersted("Not Intersted","label label-danger");
		
		String tag;
		String labelClass;
		private Status(String tag, String labelClass) {
			this.tag = tag;
			this.labelClass = labelClass;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public String getLabelClass() {
			return labelClass;
		}
		public void setLabelClass(String labelClass) {
			this.labelClass = labelClass;
		}
		
	}
	
	public static enum Priority{
		
		Low("Low","label label-default"),
		Medium("Medium","label label-info"),
		High("High","label label-warning"),
		Critical("Critical","label label-danger");
		
		String tag;
		String labelClass;
		private Priority(String tag, String labelClass) {
			this.tag = tag;
			this.labelClass = labelClass;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public String getLabelClass() {
			return labelClass;
		}
		public void setLabelClass(String labelClass) {
			this.labelClass = labelClass;
		}
	}
	
	@Column(name = "lead_id")
	String leadID;
	
	@ManyToOne
	@JoinColumn(name = "business_id")
	Business business;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	Customer customer;
	
	@Column(name = "name")
	String name;
	
	@ManyToOne
	@JoinColumn(name = "created_by")
	User createdBy;

	@ManyToOne
	@JoinColumn(name = "assigned_to")
	User assignedTo;
	
	@Column(name = "status")
	Status status = Status.Open;
	
	@Column(name = "next_followup_time")
	Date nextFollowUpTime;
	
	@Column(name = "source")
	String source;
	
	@Column(name = "priority")
	Priority priority = Priority.Low;
	
	@Column(name = "category")
	String Category;
	
	@Column(name = "comment")
	String comment;
	
	@Column(name = "important")
	boolean important;
	
	@Column(name = "extras")
	String extras;
	
	@Column(name = "deleted")
	boolean deleted;
	
	@Transient
	LeadMainExtras leadMainExtas;
	
	@Transient
	Integer contactCount;
	
	@Transient
	String mobileNumber;
	
	@Transient
	String email;
	
	@Transient
	Integer assigneeID;
	
	@Transient
	Integer activityCount;
	
	@Transient
	List<LeadActivity> activities;
	
	@Transient
	Integer assignedCount;
	
	@Transient
	Integer onBoardedCount;
	
	@Transient
	Integer prospectCount;
	
	@Transient
	Merchant merchant;
	
	@Transient
	Integer workingCount;
	
	@Transient
	Integer activityGap;
	
	@Transient
	Integer leadCount;
	
	@Transient
	String contact;
	
	@Transient
	String statusStyle;
	
	@Transient
	String priorityStyle;
	
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

	public String getLeadID() {
		return leadID;
	}

	public void setLeadID(String leadID) {
		this.leadID = leadID;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getNextFollowUpTime() {
		return nextFollowUpTime;
	}

	public void setNextFollowUpTime(Date nextFollowUpTime) {
		this.nextFollowUpTime = nextFollowUpTime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		Category = category;
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
	}

	public String getExtras() {
		return extras;
	}

	public void setExtras(String extras) {
		this.extras = extras;
		this.leadMainExtas = LeadMainExtras.fromJSON(extras);
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public LeadMainExtras getLeadMainExtas() {
		return LeadMainExtras.fromJSON(this.extras);
	}

	public void setLeadMainExtas(LeadMainExtras leadMainExtas) {
		this.leadMainExtas = leadMainExtas;
		if(leadMainExtas != null)
			extras = Utils.toJSON(leadMainExtas);
		else
			extras = null;
	}

	public Integer getContactCount() {
		return contactCount;
	}

	public void setContactCount(Integer contactCount) {
		this.contactCount = contactCount;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAssigneeID() {
		return assigneeID;
	}

	public void setAssigneeID(Integer assigneeID) {
		this.assigneeID = assigneeID;
	}

	public Integer getActivityCount() {
		return activityCount;
	}

	public void setActivityCount(Integer activityCount) {
		this.activityCount = activityCount;
	}

	public List<LeadActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<LeadActivity> activities) {
		this.activities = activities;
	}

	public Integer getAssignedCount() {
		return assignedCount;
	}

	public void setAssignedCount(Integer assignedCount) {
		this.assignedCount = assignedCount;
	}

	public Integer getOnBoardedCount() {
		return onBoardedCount;
	}

	public void setOnBoardedCount(Integer onBoardedCount) {
		this.onBoardedCount = onBoardedCount;
	}

	public Integer getProspectCount() {
		return prospectCount;
	}

	public void setProspectCount(Integer prospectCount) {
		this.prospectCount = prospectCount;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Integer getWorkingCount() {
		return workingCount;
	}

	public void setWorkingCount(Integer workingCount) {
		this.workingCount = workingCount;
	}

	public Integer getActivityGap() {
		return activityGap;
	}

	public void setActivityGap(Integer activityGap) {
		this.activityGap = activityGap;
	}

	public Integer getLeadCount() {
		return leadCount;
	}

	public void setLeadCount(Integer leadCount) {
		this.leadCount = leadCount;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getStatusStyle() {
		return statusStyle;
	}

	public void setStatusStyle(String statusStyle) {
		this.statusStyle = statusStyle;
	}

	public String getPriorityStyle() {
		return priorityStyle;
	}

	public void setPriorityStyle(String priorityStyle) {
		this.priorityStyle = priorityStyle;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
		
}

