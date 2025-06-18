package com.SCM.referral;

import java.util.Date;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.Customer;
import com.SCM.enums.Category;
import com.SCM.reward.Reward;
import com.SCM.transaction.Transaction;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class Refferal extends BigBaseEntity{
	
	public static enum Source{
		
		MERCHANT_DIRECT("Merchant Invited"),
		MERCHANT_INVITE("Merchant Invite Link"),
		CUSTOMER_DIRECT("Customer Invited"),
		CUSTOMER_INVITE("Customer Invite Link"),
		BULK_IMPORT("Bulk Import");
		
		private String tag;

		private Source(String tag) {
			this.tag = tag;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}
		
	}
	
	public static enum State {
		INVITE("Invited"),
		SALES_CONVERSION("Purchased");
		private String tag;
		private State(String tag) {
			this.tag = tag;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
	}
	
	public static enum Status{
		
		APPROVE("Approve", "label label-success"),
		REJECT("Reject","label label-danger"),
		PENDING("Pending","label label-warning");
		
		private String tag;
		private String style;
		
		private Status(String tag, String style) {
			this.tag = tag;
			this.style = style;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getStyle() {
			return style;
		}

		public void setStyle(String style) {
			this.style = style;
		}
		
	}
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	
	@ManyToOne
	@JoinColumn(name = "referrer")
	private Customer referrer;
	
	@ManyToOne
	@JoinColumn(name = "referred")
	private Customer referred;
	
	@Enumerated(EnumType.ORDINAL)
	Source source = Source.MERCHANT_DIRECT;
	
	@Enumerated(EnumType.ORDINAL)
	State state = State.INVITE;
	
	@Enumerated(EnumType.ORDINAL)
	Status status = Status.APPROVE;
	
	@OneToOne
	@JoinColumn(name = "reward_before_id")
	private Reward rewardBefore;
	
	@OneToOne
	@JoinColumn(name = "reward_after_id")
	private Reward rewardAfter;
	
	@OneToOne
	@JoinColumn(name = "welcome_reward_id")
	private Reward welcome;
	
	
	@OneToOne
	@JoinColumn(name = "transaction_id")
	private Transaction transaction;
	
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


	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Customer getReferrer() {
		return referrer;
	}

	public void setReferrer(Customer referrer) {
		this.referrer = referrer;
	}

	public Customer getReferred() {
		return referred;
	}

	public void setReferred(Customer referred) {
		this.referred = referred;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Reward getRewardBefore() {
		return rewardBefore;
	}

	public void setRewardBefore(Reward rewardBefore) {
		this.rewardBefore = rewardBefore;
	}

	public Reward getRewardAfter() {
		return rewardAfter;
	}

	public void setRewardAfter(Reward rewardAfter) {
		this.rewardAfter = rewardAfter;
	}

	public Reward getWelcome() {
		return welcome;
	}

	public void setWelcome(Reward welcome) {
		this.welcome = welcome;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
}
