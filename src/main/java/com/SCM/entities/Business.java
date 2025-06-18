package com.SCM.entities;

import java.util.Date;

import com.SCM.businessConfig.Config;
import com.SCM.enums.Category;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "business")
public class Business extends BigBaseEntity{

    @Column(name = "name_id")
	String nameID;
	
	@Column(name = "seq_id")
	String sequenceID;
	
	String code;
	
	String address;
	
	@Column(name = "full_name")
	String fullName;

	private String name;
	
	 @Column(name = "deleted", columnDefinition = "INT(11)")
	private boolean deleted= false;
	
	String description;
	
	@Enumerated(EnumType.ORDINAL)
	private Category category;

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

    public String getNameID() {
        return nameID;
    }

    public void setNameID(String nameID) {
        this.nameID = nameID;
    }

    public String getSequenceID() {
        return sequenceID;
    }

    public void setSequenceID(String sequenceID) {
        this.sequenceID = sequenceID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	
	@Transient
	Config config;
	
	@Column(name = "config_script")
	String configScript;

	public String getConfigScript() {
		return configScript;
	}

	public void setConfigScript(String configScript) {
		this.configScript = configScript;
	}

	 public Config getConfig() {
	        return Config.fromJSON(configScript);
	    }

	public void setConfig(Config config) {
		this.config = config;
		if(config != null)
			this.configScript = config.toJSON();
		else
			this.configScript = null;
	}
	
	

}
