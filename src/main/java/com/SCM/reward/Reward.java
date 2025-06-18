package com.SCM.reward;

import java.util.Date;

import com.SCM.controllers.Merchant;
import com.SCM.controllers.Region;
import com.SCM.entities.Customer;
import com.SCM.enums.Category;
import com.SCM.utils.BigBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Reward extends BigBaseEntity{
	
	public static enum RewardType{
		
		UNIVERSAL("Universal"),
		
		EXCLUSIVE("Exclusive"),
		
		COUPON("Coupon");
		
		private String tag;

		private RewardType(String tag) {
			this.tag = tag;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}
			
	}
	
	public static enum RewardMode{
		
		TRANSACTIONAL("Shopping"),OFFLINE("Gift Voucher"),PROMOTIONAL("Camaign"),
		REFERRAL_SEND("Referral Share"),REFERRAL_INVITE("Referral Invite"),REFERRAL_SALE("Referral Conversion"),
		GIFT_CARD("Gift Card"),JOINT_PROMOTION("Co-branding"),MEMBERSHIP_TIER("Tier Upfrade"),
		DIRECT_REDEMPTION("Open Coupon"),REGISTRATION("Enrolment Benefit"),PROFILE_COMPLETION("Profile Completion"),
		SIGN_IN("Sign-in Benefit"),APPOINTMENT("Appointment Booking"),FEEDBACK("Feedback Share");
		
		private String tag;

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		private RewardMode(String tag) {
			this.tag = tag;
		}
		
		
	}
	
	@Column(name = "reward_id")
	String rewardID;
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer cusomer;
	
	@ManyToOne
	@JoinColumn(name = "sender_id")
	private Customer sender;
	
	private Float amount;
	
	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	RewardType type = RewardType.UNIVERSAL;
	
	@Column(name = "mode")
	@Enumerated(EnumType.ORDINAL)
	RewardMode mode = RewardMode.TRANSACTIONAL;
	
	@Transient
	Float expiredAmount;
	
	@Transient
	Float balance;
	
	Date expiry;
	
	String note;
	
	public String getRewardID() {
		return rewardID;
	}

	public void setRewardID(String rewardID) {
		this.rewardID = rewardID;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Customer getCusomer() {
		return cusomer;
	}

	public void setCusomer(Customer cusomer) {
		this.cusomer = cusomer;
	}

	public Customer getSender() {
		return sender;
	}

	public void setSender(Customer sender) {
		this.sender = sender;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public RewardType getType() {
		return type;
	}

	public void setType(RewardType type) {
		this.type = type;
	}

	public RewardMode getMode() {
		return mode;
	}

	public void setMode(RewardMode mode) {
		this.mode = mode;
	}

	public Float getExpiredAmount() {
		return expiredAmount;
	}

	public void setExpiredAmount(Float expiredAmount) {
		this.expiredAmount = expiredAmount;
	}

	public Float getBalance() {
		return balance;
	}

	public void setBalance(Float balance) {
		this.balance = balance;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	boolean deleted = false;

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	
	
	

}
