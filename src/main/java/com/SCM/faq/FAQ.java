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
@Table(name = "faq")
public class FAQ extends BigBaseEntity{
	
	@ManyToOne
	@JoinColumn(name = "business_id")
	Business business;
	
	@ManyToOne
	@JoinColumn(name = "faq_list_id")
	FAQList faqList;
	
	@Column(name = "`order`")
	Integer order;
	
	@Column(length = 100)
    private String topic;
    
    @Column(columnDefinition = "TEXT")
    private String question;
    
    @Column(columnDefinition = "TEXT")
    private String answer;
    
    @Column(columnDefinition = "INT(11) DEFAULT 0")
    private Boolean deleted; // Represented as INT(11) in MySQL
    
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

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
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

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public FAQList getFaqList() {
		return faqList;
	}

	public void setFaqList(FAQList faqList) {
		this.faqList = faqList;
	}
	

}

