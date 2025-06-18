package com.SCM.dao;

public class MerchantUserDTO {
    private String shortLink;
    private String staffLink;
    // other properties
    
    
    

    // getters and setters
    public String getShortLink() {
        return shortLink;
    }

    public MerchantUserDTO(String shortLink, String staffLink) {
		super();
		this.shortLink = shortLink;
		this.staffLink = staffLink;
	}

	public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public String getStaffLink() {
        return staffLink;
    }

    public void setStaffLink(String staffLink) {
        this.staffLink = staffLink;
    }

    // other getters and setters
}

