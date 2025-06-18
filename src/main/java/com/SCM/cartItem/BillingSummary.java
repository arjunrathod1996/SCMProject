package com.SCM.cartItem;

import java.util.Date;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "billing_summary")
public class BillingSummary extends BigBaseEntity{
	
	private Double totalAmount;
    
	@ManyToOne
	@JoinColumn(name = "user_staff_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
    
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

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	@Override
	public String toString() {
		return "BillingSummary [totalAmount=" + totalAmount + ", user=" + user + ", merchant=" + merchant + "]";
	}

}
