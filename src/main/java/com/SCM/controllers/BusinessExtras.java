package com.SCM.controllers;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;

public class BusinessExtras {
	String registeredName;
	String PAN;
	String GSTIN;
	String logoImage;
	//MessageTemplate messageTemplate;
	//WhatsappConfig whatsappConfig ;
	
	boolean disableVisibility;
	
	boolean disablePartnerPage;
	
	Integer minRating;
	
	boolean starRatingView = true;
	
	//private Branding branding;
	
	private Boolean enableHideBanner;
	
	public static BusinessExtras fromJSON(String jsonData) {
		BusinessExtras businessExtras = null;
		try {
			businessExtras = new Gson().fromJson(jsonData,BusinessExtras.class);
		
		}catch(JsonSyntaxException ex) {
			ex.printStackTrace();
		}
		
		return businessExtras;
	}
	
	public String toJson() {
		return new Gson().toJson(this);
	}

	public String getRegisteredName() {
		return registeredName;
	}

	public void setRegisteredName(String registeredName) {
		this.registeredName = registeredName;
	}

	public String getPAN() {
		return PAN;
	}

	public void setPAN(String pAN) {
		PAN = pAN;
	}

	public String getGSTIN() {
		return GSTIN;
	}

	public void setGSTIN(String gSTIN) {
		GSTIN = gSTIN;
	}

	public String getLogoImage() {
		return logoImage;
	}

	public void setLogoImage(String logoImage) {
		this.logoImage = logoImage;
	}

	public boolean isDisableVisibility() {
		return disableVisibility;
	}

	public void setDisableVisibility(boolean disableVisibility) {
		this.disableVisibility = disableVisibility;
	}

	public boolean isDisablePartnerPage() {
		return disablePartnerPage;
	}

	public void setDisablePartnerPage(boolean disablePartnerPage) {
		this.disablePartnerPage = disablePartnerPage;
	}

	public Integer getMinRating() {
		return minRating;
	}

	public void setMinRating(Integer minRating) {
		this.minRating = minRating;
	}

	public boolean isStarRatingView() {
		return starRatingView;
	}

	public void setStarRatingView(boolean starRatingView) {
		this.starRatingView = starRatingView;
	}

//	public Branding getBranding() {
//		return branding;
//	}
//
//	public void setBranding(Branding branding) {
//		this.branding = branding;
//	}

	public Boolean getEnableHideBanner() {
		return enableHideBanner;
	}

	public void setEnableHideBanner(Boolean enableHideBanner) {
		this.enableHideBanner = enableHideBanner;
	}
}
