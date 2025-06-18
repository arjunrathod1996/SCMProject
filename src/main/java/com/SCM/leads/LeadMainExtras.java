package com.SCM.leads;

import java.util.List;

import com.nimbusds.jose.shaded.gson.Gson;

public class LeadMainExtras {
	
	private String aboutUs;
	private String address;
	List<LeadContactOld> contacts;
	
	public static LeadMainExtras fromJSON(String extrasScript) {
		LeadMainExtras extras = null;
		
		try {
			extras = new Gson().fromJson(extrasScript, LeadMainExtras.class);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return extras;
	}
	
	public String toJSON() {
		return new Gson().toJson(this);
	}

	public String getAboutUs() {
		return aboutUs;
	}

	public void setAboutUs(String aboutUs) {
		this.aboutUs = aboutUs;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<LeadContactOld> getContacts() {
		return contacts;
	}

	public void setContacts(List<LeadContactOld> contacts) {
		this.contacts = contacts;
	}
	
	

}
