package com.SCM.twilioConfig;

public class OtpAuthenticationToken {
	
	private String otp;
	private String phoneNumber;
	
	
	public OtpAuthenticationToken(String otp, String phoneNumber) {
		super();
		this.otp = otp;
		this.phoneNumber = phoneNumber;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	

}
