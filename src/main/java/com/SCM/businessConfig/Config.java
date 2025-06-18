package com.SCM.businessConfig;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;

public class Config {
    Boolean enableGiftVoucher;
    Boolean enableI18nInputSupport;
    Boolean enableQRCode;
    Boolean enablePaytmVoucher;
    Boolean enableSameOriginReward;
    Boolean enableBillSumbission;
    Boolean enableAppointment;

    public Boolean getEnableGiftVoucher() {
		return enableGiftVoucher;
	}

	public void setEnableGiftVoucher(Boolean enableGiftVoucher) {
		this.enableGiftVoucher = enableGiftVoucher;
	}

	public Boolean getEnableI18nInputSupport() {
		return enableI18nInputSupport;
	}

	public void setEnableI18nInputSupport(Boolean enableI18nInputSupport) {
		this.enableI18nInputSupport = enableI18nInputSupport;
	}

	public Boolean getEnableQRCode() {
		return enableQRCode;
	}

	public void setEnableQRCode(Boolean enableQRCode) {
		this.enableQRCode = enableQRCode;
	}

	public Boolean getEnablePaytmVoucher() {
		return enablePaytmVoucher;
	}

	public void setEnablePaytmVoucher(Boolean enablePaytmVoucher) {
		this.enablePaytmVoucher = enablePaytmVoucher;
	}

	public Boolean getEnableSameOriginReward() {
		return enableSameOriginReward;
	}

	public void setEnableSameOriginReward(Boolean enableSameOriginReward) {
		this.enableSameOriginReward = enableSameOriginReward;
	}

	public Boolean getEnableBillSumbission() {
		return enableBillSumbission;
	}

	public void setEnableBillSumbission(Boolean enableBillSumbission) {
		this.enableBillSumbission = enableBillSumbission;
	}

	public Boolean getEnableAppointment() {
		return enableAppointment;
	}

	public void setEnableAppointment(Boolean enableAppointment) {
		this.enableAppointment = enableAppointment;
	}

	// Static methods for JSON serialization and deserialization
    public static Config fromJSON(String jsonData) {
    	Config config = null;
       try {
    	   config = new Gson().fromJson(jsonData, Config.class);
       }catch(JsonSyntaxException ex) {
    	   ex.printStackTrace();
       }
       return config;
    }

    public String toJSON() {
		return new Gson().toJson(this);
	}

    // Static method to get default config
    public static Config getDefault() {
        Config config = new Config();
        config.setEnableGiftVoucher(false);
        config.setEnablePaytmVoucher(false);
        config.setEnableI18nInputSupport(false);
        config.setEnableQRCode(false);
        config.setEnableSameOriginReward(false);
        config.setEnableAppointment(false);
        config.setEnableBillSumbission(false);
        return config;
    }
}
