package com.SCM.role;

import java.util.Date;

import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;




@Entity
@Table(name = "role")
public class Role extends BigBaseEntity {
  
  public static enum RoleType {
      ROLE_ADMIN("Admin"),
      ROLE_CUSTOMER("Customer"),
      ROLE_MERCHANT("Merchant"),
      ROLE_MERCHANT_STAFF("Merchant Staff"),
      ROLE_MERCHANT_ADMIN("Merchant Admin"),
      ROLE_ASSOCIATE("Associate");

	  private String tag;

	    private RoleType(String tag) {
	        this.tag = tag;
	    }

	    public String getTag() {
	        return tag;
	    }

	    public void setTag(String tag) {
	        this.tag = tag;
	    }

	    public String getName() {
	        return name();
	    }
	    
	    public static RoleType fromString(String text) {
	        for (RoleType role : RoleType.values()) {
	            if (role.tag.equalsIgnoreCase(text)) {
	                return role;
	            }
	        }
	        throw new IllegalArgumentException("No constant with text " + text + " found");
	    }
	    
	    
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

  @Enumerated(EnumType.STRING)
 @Column(name = "role_name")
 // @JsonDeserialize(using = RoleTypeDeserializer.class)  // Register the custom deserializer
  private RoleType name;

   @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @JoinTable(name = "role_permission", 
             joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), 
             inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
  private java.util.Set<Permission> permissions;

  public RoleType getName() {
      return name;
  }

  public void setName(RoleType name) {
      this.name = name;
  }

  public java.util.Set<Permission> getPermissions() {
      return permissions;
  }

  public void setPermissions(java.util.Set<Permission> permissions) {
      this.permissions = permissions;
  }

@Override
public String toString() {
	return "Role [name=" + name + ", permissions=" + permissions + "]";
}
  
  
}
