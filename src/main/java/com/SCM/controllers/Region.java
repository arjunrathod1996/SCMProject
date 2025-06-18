package com.SCM.controllers;

import java.util.Date;

import org.springframework.stereotype.Controller;

import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "region")
public class Region extends BigBaseEntity{
	
	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	public Country getCountry() {
		return country;
	}
	
	String city;
	
	String state;
	
	String zone;
	
	@Transient
	private Long countryID;
	
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

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public Long getCountryID() {
		return countryID;
	}

	public void setCountryID(Long countryID) {
		this.countryID = countryID;
	}
	
	
}
