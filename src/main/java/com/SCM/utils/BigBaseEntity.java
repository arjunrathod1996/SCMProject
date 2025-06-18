package com.SCM.utils;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@MappedSuperclass
public abstract class BigBaseEntity {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//@JsonSerialize(using = CustomDateSerializer.class)
	@Column(name = "creation_time")
	@Temporal(TemporalType.DATE)
	private Date creationTime;
	
	//@JsonSerialize(using = CustomDateSerializer.class)
	@Column(name = "update_time")
	@Temporal(TemporalType.DATE)
	private Date updateTime;
	
	protected void onCreate() {
		
		if(this.getCreationTime() == null)
			this.setCreationTime(Utils.now());
	}
	
	public Long getId() {
		
		return id;
	}
	
	public void setId(Long id) {
		
		this.id = id;
	}
	
	
	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		
		return result;
		
	}
	
	public boolean equals(Object obj) {
		
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		BigBaseEntity other = (BigBaseEntity) obj;
		if(id == null) {
			
			if(other.id != null)
				return false;
		}else if(!id.equals(other.id))
			return false;
		
		return true;
	
	}
	
}





