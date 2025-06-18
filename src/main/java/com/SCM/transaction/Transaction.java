package com.SCM.transaction;

import java.util.Date;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.reward.Reward;
import com.SCM.utils.BigBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Transaction extends BigBaseEntity{
	
	public static enum Status{
		STATUS_PENDING("Pending"),
		STATUS_APPROVED("Approve"),
		STATUS_REVIEW("Review"),
		STATUS_DECLINED("Declined");
		
		private String tag;

		private Status(String tag) {
			this.tag = tag;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}
	}
	
	public static enum RewardState{
		
		STARTED("Start"),
		EVALUATE("Evaluate"),
		SUCCESS("Success"),
		FAIL_NOT_APPLICABLE("Not Applicable"),
		FAIL_NO_BALANCE("No Balance"),
		NO_REWARD_FOUND("No Reward Found"),
		SKIP_REWARD("Skip Reward"),
		COUPON_ISSUED("Coupon Issued"),
		COUPON_NOT_FOUND("No Coupon Found");
		
		private String tag;
		
		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		private RewardState(String tag) {
			this.tag = tag;
		}
	
	}
	
	public static enum PaymentStatus{
		
		PAYMENT_PENDING("Pending"),
		PAYMENT_SUCCESS("Success"),
		PAYMENT_FAILED("Failed");
		
		private String tag;
		
		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		private PaymentStatus(String tag) {
			this.tag = tag;
		}
		
	}

	public static enum DeliveryStatus{
		DELIVERY_PENDING("Pending"),
		DELIVERY_PROCESSING("Processing"),
		DELIVERY_COMPLETE("Complete"),
		DELIVERY_CANCELLED("Cancelled");
		
		private String tag;
		
		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		private DeliveryStatus(String tag) {
			this.tag = tag;
		}
	
	}
	
	@Column(name = "transaction_id")
	String transactionID;
	
	@Column(name = "seq_id")
	Integer sequenceID;
	
	@Column(name = "entry_key")
	String entryKey;
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "created_by")
	private User createdBy;
	
	@ManyToOne
	@JoinColumn(name = "reward_id")
	private Reward reward;
	
	// Transaction Amount
	Float value;
	
	String note;
	
	Float tax;
	
	Float Discount;
	
	@Column(name = "credit_note")
	Float creditNote;
	
	@Enumerated(EnumType.ORDINAL)
	RewardState rewardState = RewardState.STARTED;
	
	@Column(name = "with_rule")
	Boolean withRule = false;
	
	@Enumerated(EnumType.ORDINAL)
	Status status = Status.STATUS_PENDING;
	
	@Column(name = "payment_status")
	@Enumerated(EnumType.ORDINAL)
	PaymentStatus paymentStatus ;
	
	@Column(name = "delivery_status")
	@Enumerated(EnumType.ORDINAL)
	DeliveryStatus deliveryStatus ;
	
	@Column(name = "deleted")
	boolean deleted;
	
	@Column(name = "delivery_time")
	@Temporal(TemporalType.TIMESTAMP)
	Date deliveryTime;

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public Integer getSequenceID() {
		return sequenceID;
	}

	public void setSequenceID(Integer sequenceID) {
		this.sequenceID = sequenceID;
	}

	public String getEntryKey() {
		return entryKey;
	}

	public void setEntryKey(String entryKey) {
		this.entryKey = entryKey;
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

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Reward getReward() {
		return reward;
	}

	public void setReward(Reward reward) {
		this.reward = reward;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Float getTax() {
		return tax;
	}

	public void setTax(Float tax) {
		this.tax = tax;
	}

	public Float getDiscount() {
		return Discount;
	}

	public void setDiscount(Float discount) {
		Discount = discount;
	}

	public Float getCreditNote() {
		return creditNote;
	}

	public void setCreditNote(Float creditNote) {
		this.creditNote = creditNote;
	}

	public RewardState getRewardState() {
		return rewardState;
	}

	public void setRewardState(RewardState rewardState) {
		this.rewardState = rewardState;
	}

	public Boolean isWithRule() {
	    return withRule;
	}

	public void setWithRule(Boolean withRule) {
	    this.withRule = withRule;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
}

