package com.SCM.twilioConfig;

public class AuthenticationRequest {
    private String mobileNumber;
    private String otp;

    // Constructors
    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String mobileNumber, String otp) {
        this.mobileNumber = mobileNumber;
        this.otp = otp;
    }

    // Getters and setters
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
