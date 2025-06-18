package com.SCM.twilioConfig;

public class TwilioDTO {

    private String phoneNumer; // destination
    private String userName;
    private String oneTimePassword;
    
    public String getPhoneNumer() {
        return phoneNumer;
    }
    public void setPhoneNumer(String phoneNumer) {
        this.phoneNumer = phoneNumer;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getOneTimePassword() {
        return oneTimePassword;
    }
    public void setOneTimePassword(String oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }

   

}
