package com.SCM.chartandFile;

import com.SCM.businessConfig.Config;
import com.SCM.entities.Business;
import com.SCM.entities.User;

public class FeatureFlagUtils {
	
	
	private static Config getConfig(Business business) {
		
		Config config = null;
		
		config = business.getConfig();
		
		return config;
		
	}
	
	
	public static boolean isEnableI18nInputSupport_(Business business) {
		
		Config config = getConfig(business);
		
		return isEnableI18nInputSupport(config);
		
	}
	
	public static boolean isEnableI18nInputSupport(Config config) {
		
		if(config != null
				&& config.getEnableI18nInputSupport() != null
				&& config.getEnableI18nInputSupport())
			return true;
		return false;
		
	}
	
	public static boolean isQrCodeEnabled(Config config) {
		
		if(config != null
				&& config.getEnableQRCode() != null
				&& config.getEnableQRCode())
			return true;
		return false;
		
	}
	
public static boolean isQrCodeEnabled_(User user) {
		
		if(user.getBusiness() == null)
			return false;
		
		Config config = getConfig(user);
		return isQrCodeEnabled(config);
		
	}

public static Config getConfig(User user) {
    if (user.getBusiness() == null && user.getMerchant().getBusiness() == null) {
        return null; // or throw an exception, depending on your requirements
    }
    
    Config config =null;
    
    if(user.getMerchant() !=null) {
    	config = user.getMerchant().getBusiness().getConfig();
    }else if(user.getBusiness() != null) {
    	config = user.getBusiness().getConfig();
    }
    
    return config;
}


}

