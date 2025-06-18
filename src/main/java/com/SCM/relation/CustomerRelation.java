package com.SCM.relation;

import java.util.Date;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Customer;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_relation")
public class CustomerRelation extends BigBaseEntity {
	
	public static enum Source {
		
		LEAD,TRANSACTION,REWARD,REDEMPTOIN,REFERRAL,GIFT_CARD;
		
	}
	
	public static enum AquisitionMode {
		
		DIRECT,
		REFERRAL,
		GIFT_CARD,
		JOIN_PROMOTION;
		
	}
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@Enumerated(EnumType.ORDINAL)
	Source source = Source.LEAD;
	
	@Enumerated(EnumType.ORDINAL)
	AquisitionMode aquisitionMode = AquisitionMode.DIRECT;
	
	Float leagacyLifetimePurchase;
	
	Integer leagacyLifetimePurchaseCount;
	
	Float legacyLifetimePoints;
	
	Float legacyRedemedPoints;
	
	Float legacyExpiredPoints;
	
	Date legacyFirstTransactionTime;
	
	Date legacyLastTransactionTime;
	
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

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public AquisitionMode getAquisitionMode() {
		return aquisitionMode;
	}

	public void setAquisitionMode(AquisitionMode aquisitionMode) {
		this.aquisitionMode = aquisitionMode;
	}

	public Float getLeagacyLifetimePurchase() {
		return leagacyLifetimePurchase;
	}

	public void setLeagacyLifetimePurchase(Float leagacyLifetimePurchase) {
		this.leagacyLifetimePurchase = leagacyLifetimePurchase;
	}

	public Integer getLeagacyLifetimePurchaseCount() {
		return leagacyLifetimePurchaseCount;
	}

	public void setLeagacyLifetimePurchaseCount(Integer leagacyLifetimePurchaseCount) {
		this.leagacyLifetimePurchaseCount = leagacyLifetimePurchaseCount;
	}

	public Float getLegacyLifetimePoints() {
		return legacyLifetimePoints;
	}

	public void setLegacyLifetimePoints(Float legacyLifetimePoints) {
		this.legacyLifetimePoints = legacyLifetimePoints;
	}

	public Float getLegacyRedemedPoints() {
		return legacyRedemedPoints;
	}

	public void setLegacyRedemedPoints(Float legacyRedemedPoints) {
		this.legacyRedemedPoints = legacyRedemedPoints;
	}

	public Float getLegacyExpiredPoints() {
		return legacyExpiredPoints;
	}

	public void setLegacyExpiredPoints(Float legacyExpiredPoints) {
		this.legacyExpiredPoints = legacyExpiredPoints;
	}

	public Date getLegacyFirstTransactionTime() {
		return legacyFirstTransactionTime;
	}

	public void setLegacyFirstTransactionTime(Date legacyFirstTransactionTime) {
		this.legacyFirstTransactionTime = legacyFirstTransactionTime;
	}

	public Date getLegacyLastTransactionTime() {
		return legacyLastTransactionTime;
	}

	public void setLegacyLastTransactionTime(Date legacyLastTransactionTime) {
		this.legacyLastTransactionTime = legacyLastTransactionTime;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	

}

