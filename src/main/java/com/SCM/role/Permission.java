package com.SCM.role;

import java.util.Date;

import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "permission")
public class Permission extends BigBaseEntity{
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return "Permission [name=" + name + "]";
	}
}