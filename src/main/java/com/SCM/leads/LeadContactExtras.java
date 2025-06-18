package com.SCM.leads;

import com.nimbusds.jose.shaded.gson.Gson;

public class LeadContactExtras {
	
	String profileLink; // eg. linkedin link 
	
	public static LeadContactExtras fromJSON(String extrasScript) {
		LeadContactExtras extras = null;
		
		try {
			extras = new Gson().fromJson(extrasScript, LeadContactExtras.class);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return extras;
	}
	
	public String toJSON() {
		return new Gson().toJson(this);
	}

	public String getProfileLink() {
		return profileLink;
	}

	public void setProfileLink(String profileLink) {
		this.profileLink = profileLink;
	}
	
	

}

