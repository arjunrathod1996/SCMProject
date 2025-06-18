package com.SCM.faq;

import java.util.Date;

import com.SCM.entities.Business;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "faq_list")
public class FAQList extends BigBaseEntity{
	
	@Column(name = "`order`")
	Integer order;
	
	@Column(length = 100)
    private String topic;
	
	@ManyToOne
	@JoinColumn(name = "business_id")
	Business business;

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

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}
}
