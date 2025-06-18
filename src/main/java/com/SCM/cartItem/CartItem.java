package com.SCM.cartItem;

import java.util.Date;

import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.photo.PhotoMerchant;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "cart_item")
public class CartItem extends BigBaseEntity {
	
	public enum CartItemStatus {
        PENDING("Pending"),
        COMPLETED("Completed"),
        CANCELED("Cancelled");
        
        private String tag;

		private CartItemStatus(String tag) {
			this.tag = tag;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}
		
		
    }
    
    @ManyToOne
    private Customer customer;

    @ManyToOne
    private PhotoMerchant photoMerchant;

    private int quantity;
    
    // Add price and amount fields
    private double price;
    private double amount;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private CartItemStatus status;
    
    
    @Column(name = "rating")
    private Integer rating;
    
    @Column(name = "revise")
    private Integer revise;
    
    @Column(name = "review_text")
    private String reviewText;

    @Column(name = "deleted")
    private boolean deleted = false;
    
    // Calculate the amount based on the quantity and price
    private double calculateAmount() {
        return quantity * price;
    }
    
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public PhotoMerchant getPhotoMerchant() {
		return photoMerchant;
	}

	public void setPhotoMerchant(PhotoMerchant photoMerchant) {
		this.photoMerchant = photoMerchant;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public CartItemStatus getStatus() {
		return status;
	}

	public void setStatus(CartItemStatus status) {
		this.status = status;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Integer getRevise() {
		return revise;
	}

	public void setRevise(Integer revise) {
		this.revise = revise;
	}

	public String getReviewText() {
		return reviewText;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Transient
	InvoiceExtras invoiceExtras;
	
	@Column(name = "invoice_extras")
	String invoiceExtrasScript;

	public InvoiceExtras getInvoiceExtras() {
		return invoiceExtras;
	}

	public void setInvoiceExtras(InvoiceExtras invoiceExtras) {
		this.invoiceExtras = invoiceExtras;
	}

	public String getInvoiceExtrasScript() {
		return invoiceExtrasScript;
	}

	public void setInvoiceExtrasScript(String invoiceExtrasScript) {
		this.invoiceExtrasScript = invoiceExtrasScript;
	}

}