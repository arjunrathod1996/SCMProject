package com.SCM.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.SCM.controllers.Merchant;
import com.SCM.role.Permission;
import com.SCM.role.Role;
import com.SCM.role.Role.RoleType;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BigBaseEntity { // implements UserDetails
	
	@ManyToOne
	@JoinColumn(name = "business_id")
	private Business business;
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true) //, nullable = true
    private String email;

    @Getter(AccessLevel.NONE)
    private String password;
    private String phoneNumber;

    @Column(length = 1000)
    private String profilePic;
    @Getter(AccessLevel.NONE) 
    private boolean enabled = true;
    private boolean emailVerified = false;
    private boolean phoneVerfied = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Providers provider = Providers.SELF;

    @Column(name = "provider_user_id")  
    private String providerUserId;

	private String otp;

	@Column(name = "staff_link")
	private String staffLink;

    
//    @ManyToOne
//    @Enumerated(EnumType.ORDINAL)
//    @JoinColumn(name = "role_id")
//    private Role role;
	
	  	@ManyToOne
	    @Enumerated(EnumType.ORDINAL)
	    @JoinColumn(name = "role_id")
	    private Role role;
	  	
	  	@Transient
		String roleName;


		public String getRoleName() {
			return roleName;
		}

		public void setRoleName(String roleName) {
			this.roleName = roleName;
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

//        @ElementCollection(fetch = FetchType.EAGER)
//        private List<String> roleList = new ArrayList<>();

//        @Override
//        public Collection<? extends GrantedAuthority> getAuthorities() {
//            // list of roles[USER,ADMIN]
//            // collection of SimpleGrantedAuthority [roles{ADMIN,USER}]
//            Collection<SimpleGrantedAuthority> roles = roleList.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
//            return roles;
//        }
//
//
//        @Override
//        public String getUsername() {
//            return this.email;
//        }
//
//        @Override
//        public boolean isAccountNonExpired(){
//            return true;
//        }
//
//        @Override
//        public boolean isAccountNonLocked(){
//            return true;
//        }
//
//        @Override
//        public boolean isCredentialsNonExpired(){
//            return true;
//        }
//
//        @Override
//        public boolean isEnabled() {
//            return this.enabled;
//        }
//
//        @Override
//        public String getPassword() {
//            return this.password;
//        }

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getOtp() {
			return otp;
		}

		public void setOtp(String otp) {
			this.otp = otp;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		public String getProfilePic() {
			return profilePic;
		}

		public void setProfilePic(String profilePic) {
			this.profilePic = profilePic;
		}

		public boolean isEmailVerified() {
			return emailVerified;
		}

		public void setEmailVerified(boolean emailVerified) {
			this.emailVerified = emailVerified;
		}

		public boolean isPhoneVerfied() {
			return phoneVerfied;
		}

		public void setPhoneVerfied(boolean phoneVerfied) {
			this.phoneVerfied = phoneVerfied;
		}

		public Providers getProvider() {
			return provider;
		}

		public void setProvider(Providers provider) {
			this.provider = provider;
		}

		public String getProviderUserId() {
			return providerUserId;
		}

		public void setProviderUserId(String providerUserId) {
			this.providerUserId = providerUserId;
		}

//		public List<String> getRoleList() {
//			return roleList;
//		}
//
//		public void setRoleList(List<String> roleList) {
//			this.roleList = roleList;
//		}

		public void setPassword(String password) {
			this.password = password;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getPassword() {
			return password;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public Role getRole() {
			return role;
		}

		public void setRole(Role role) {
			this.role = role;
		}

		public Business getBusiness() {
			return business;
		}

		public void setBusiness(Business business) {
			this.business = business;
		}

		public Merchant getMerchant() {
			return merchant;
		}

		public void setMerchant(Merchant merchant) {
			this.merchant = merchant;
		}

		public String getStaffLink() {
			return staffLink;
		}

		public void setStaffLink(String staffLink) {
			this.staffLink = staffLink;
		}

		public Customer getCustomer() {
			return customer;
		}

		public void setCustomer(Customer customer) {
			this.customer = customer;
		}

		@Override
		public String toString() {
			return "User [business=" + business + ", merchant=" + merchant + ", customer=" + customer + ", userId="
					+ userId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
					+ ", password=" + password + ", phoneNumber=" + phoneNumber + ", profilePic=" + profilePic
					+ ", enabled=" + enabled + ", emailVerified=" + emailVerified + ", phoneVerfied=" + phoneVerfied
					+ ", provider=" + provider + ", providerUserId=" + providerUserId + ", otp=" + otp + ", staffLink="
					+ staffLink + ", role=" + role + ", roleName=" + roleName + "]";
		}
		
		
}
