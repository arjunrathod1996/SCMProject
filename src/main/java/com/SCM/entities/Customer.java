package com.SCM.entities;

import java.util.Date;

import com.SCM.controllers.Country;
import com.SCM.controllers.Region;
import com.SCM.photo.Photo;
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
import jakarta.persistence.Transient;

@Entity
@Table(name = "customer")
public class Customer extends BigBaseEntity{
	
	
	public static enum Gender {
	    // Clothing categories
	    MALE("Male"),
	    FEMALE("Female");
	   
	    private final String displayGender;
	  
		private Gender(String displayGender) {
			this.displayGender = displayGender;
		}

		public String getDisplayGender() {
			return displayGender;
		}

	}
	
	@Column(name = "first_name")
	String firstName;
	
	@Column(name = "mobile_number")
	String mobileNumber;
	
	@ManyToOne  
	@JoinColumn(name = "country_id")
	Country country;
	
	@ManyToOne
	@JoinColumn(name = "region_id")
	Region region;
	
	@Column(name = "email")
	String email;
	
	@Column(name = "last_name")
	String lastName;
	
	@ManyToOne
	@JoinColumn(name = "photo_id")
	Photo photo;
	
	@Transient
	Long countryID;
	
	@Column(name = "gender")
	@Enumerated(EnumType.ORDINAL)
	private Gender gender;
	
	@Column(name = "birthday")
	private String bithday;
	
	@PrePersist
    protected void onCreate() {
        Date now = Utils.now();
        if (this.getCreationTime() == null)
            this.setCreationTime(now);
        if (this.getUpdateTime() == null)
            this.setUpdateTime(now);
    }

    @PreUpdate
    protected void onUpdate() {
        this.setUpdateTime(Utils.now());
    }

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public Long getCountryID() {
		return countryID;
	}

	public void setCountryID(Long countryID) {
		this.countryID = countryID;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getBithday() {
		return bithday;
	}

	public void setBithday(String bithday) {
		this.bithday = bithday;
	}
	
	
}

