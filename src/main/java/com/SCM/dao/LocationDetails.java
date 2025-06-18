package com.SCM.dao;

public class LocationDetails {
	
	private String country;
	private String state;
	private String city;
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@Override
	public String toString() {
		return "LocationDetails [country=" + country + ", state=" + state + ", city=" + city + "]";
	}
	
	

}
