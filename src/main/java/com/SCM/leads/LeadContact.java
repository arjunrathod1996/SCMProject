package com.SCM.leads;

import java.util.Date;

import com.SCM.entities.Business;
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
@Table(name = "lead_contact")
public class LeadContact extends BigBaseEntity{

	@ManyToOne
	@JoinColumn(name = "business_id")
	Business business;
	
	@ManyToOne
	@JoinColumn(name = "lead_id")
	LeadMain leadMain;
	
	@Column(name ="first_name")
	String firstName;
	
	@Column(name ="last_name")
	String lastName;
	
	@Column(name = "designation")
	String designation;
	
	// unique primary phone number is required
	
	@Column(name = "phone_number")
	String phoneNumber;
	
	@Column(name = "phone_number2")
	String phoneNumber2;
	
	@Column(name = "email")
	String email;
	
	@Column(name = "location")
	String location;
	
	@Column(name="note")
	String note;
	
	@Column(name = "important")
	boolean important;
	
	@ManyToOne
	@JoinColumn(name = "created_by")
	User createdBy;
	
	@ManyToOne
	@JoinColumn(name = "updated_by")
	User updatedBy;
	
	@Column(name = "deleted")
	boolean deleted;
	
	@Column(name = "extras")
	String extras;
	
	@Transient
	LeadContactExtras contactExtras;
	
	@Column(name = "contact_id")
	private String contactID;
	
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

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public LeadMain getLeadMain() {
		return leadMain;
	}

	public void setLeadMain(LeadMain leadMain) {
		this.leadMain = leadMain;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber2() {
		return phoneNumber2;
	}

	public void setPhoneNumber2(String phoneNumber2) {
		this.phoneNumber2 = phoneNumber2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getExtras() {
		return extras;
	}

	public void setExtras(String extras) {
		this.extras = extras;
		this.contactExtras = LeadContactExtras.fromJSON(extras);
		
	}

	public LeadContactExtras getContactExtras() {
		return LeadContactExtras.fromJSON(this.extras);
	}

	public void setContactExtras(LeadContactExtras contactExtras) {
		this.contactExtras = contactExtras;
		if(contactExtras != null)
			extras = contactExtras.toJSON();
		else
			extras = null;
	}

	public String getContactID() {
		return contactID;
	}

	public void setContactID(String contactID) {
		this.contactID = contactID;
	}

}
