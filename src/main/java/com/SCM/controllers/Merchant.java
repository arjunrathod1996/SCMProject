package com.SCM.controllers;

import java.util.Date;

import com.SCM.entities.Business;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;
import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "merchant")
public class Merchant extends BigBaseEntity{

	@ManyToOne
	@JoinColumn(name = "business_id")
	private Business business;
	
	@ManyToOne
	@JoinColumn(name = "region_id")
	private Region region;
	
	@Column(name = "seq_id")
	Integer sequenceID;
	
	@Column(name = "mobile_number")
	String mobileNumber;
	
	@Column(name = "display_phone")
	String displayPhone;
	
	@JsonCreator
    public static Merchant create() {
        return new Merchant();
    }
	
	String locality;
	
	String address;
	
	String alias;
	
	boolean active = true;
	
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
	
	@Transient
	BusinessExtras businessExtras;
	
	@Column(name = "business_extras")
	String businessExtrasScript;

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}
	
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "main_outlet")
	boolean mainOutlet = true;
	
	@Column(name = "short_link")
	String shortLink;
	
	public boolean isMainOutlet() {
		return mainOutlet;
	}

	public void setMainOutlet(boolean mainOutlet) {
		this.mainOutlet = mainOutlet;
	}
	
	@Transient
	Float balances;
	
	@Transient
	Float totalRecharge;
	
	
	@Transient
	Float totalCharge;
	
	@Transient
	Float voucherCredit;
	
	@Transient
	Float Promo;

	public Float getBalances() {
		return balances;
	}

	public void setBalances(Float balances) {
		this.balances = balances;
	}

	public Float getTotalRecharge() {
		return totalRecharge;
	}

	public void setTotalRecharge(Float totalRecharge) {
		this.totalRecharge = totalRecharge;
	}

	public Float getTotalCharge() {
		return totalCharge;
	}

	public void setTotalCharge(Float totalCharge) {
		this.totalCharge = totalCharge;
	}

	public Float getVoucherCredit() {
		return voucherCredit;
	}

	public void setVoucherCredit(Float voucherCredit) {
		this.voucherCredit = voucherCredit;
	}

	public Float getPromo() {
		return Promo;
	}

	public void setPromo(Float promo) {
		Promo = promo;
	}
	
	
	@Transient
	Long depositID;
	
	public Long getDepositID() {
		return depositID;
	}

	public void setDepositID(Long depositID) {
		this.depositID = depositID;
	}

	@Transient
	Long merchantID;

	public Long getMerchantID() {
		return merchantID;
	}

	public void setMerchantID(Long merchantID) {
		this.merchantID = merchantID;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public String getShortLink() {
		return shortLink;
	}

	public void setShortLink(String shortLink) {
		this.shortLink = shortLink;
	}

	
	String image;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	String smallImage;

	public String getSmallImage() {
		return smallImage;
	}

	public void setSmallImage(String smallImage) {
		this.smallImage = smallImage;
	}

	public Integer getSequenceID() {
		return sequenceID;
	}

	public void setSequenceID(Integer sequenceID) {
		this.sequenceID = sequenceID;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getDisplayPhone() {
		return displayPhone;
	}

	public void setDisplayPhone(String displayPhone) {
		this.displayPhone = displayPhone;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public BusinessExtras getBusinessExtras() {
		return BusinessExtras.fromJSON(businessExtrasScript);
	}

	public void setBusinessExtras(BusinessExtras businessExtras) {
		this.businessExtras = businessExtras;
		if(businessExtras != null)
			this.businessExtrasScript = businessExtras.toJson();
		else
			this.businessExtrasScript = null;
	}

	public String getBusinessExtrasScript() {
		return businessExtrasScript;
	}

	public void setBusinessExtrasScript(String businessExtrasScript) {
		this.businessExtrasScript = businessExtrasScript;
		this.businessExtras = BusinessExtras.fromJSON(businessExtrasScript);
	}
	
}
