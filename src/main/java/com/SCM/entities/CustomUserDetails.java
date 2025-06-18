package com.SCM.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User user;
     
	    public CustomUserDetails(User user) {
	        this.user = user;
	    }
	 
	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        Set<GrantedAuthority> authorities = new HashSet<>();

	        // Add role as an authority
	        authorities.add(new SimpleGrantedAuthority(user.getRole().getName().name()));

	        // Add permissions as authorities
	        user.getRole().getPermissions().stream()
	            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
	            .forEach(authorities::add);

	        return authorities;
	    }
	 
	    @Override
	    public String getPassword() {
	        return user.getPassword();
	    }
	 
	    @Override
	    public String getUsername() {
	        return user.getEmail();
	    }
	 
	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }
	 
	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }
	 
	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }
	 
	    @Override
	    public boolean isEnabled() {
	        return user.isEnabled();
	    }
	 
	}