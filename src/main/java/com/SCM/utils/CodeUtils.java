package com.SCM.utils;

import java.util.Random;

public class CodeUtils {
	
	private static final String RECORD_ID_FORMAT = "%s-%s-%s";
	private static final String SIMPLE_RECORD_ID_FORMAT = "%s-%s";
	
	public static final char[] CHAR_SET = {
			
			'0','1','2','3','4','5','6','7','8','9',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
			't', 'u', 'v', 'w', 'x', 'y', 'z'
			
	};
	
	public static String getTimeStampID() {
		
		return toAlphaNumeric(Utils.now().getTime());
		
	}
	
	public static String getRecordID(Long id) {
		
		return getRecordID(id,null,false);
		
	}
	
	public static String getRecordID(Long id,Integer random) {
		
		return getRecordID(id,null,false);
		
	}
	
public static String geSimpleRecordID(Long id,Integer random) {
		
		return getRecordID(id,null,false);
		
	}

public static String geSimpleRecordID(Long id,Long timestamp) {
	String idStr = toAlphaNumeric(id);
	String timeStr = toAlphaNumeric(timestamp);
	
	
	return String.format(SIMPLE_RECORD_ID_FORMAT, idStr,timeStr);
	
}

private static String getRecordID(Long id,Integer random,boolean includeRandom) {
	
	String idStr = toAlphaNumeric(id);
	
	long timestamp = Utils.now().getTime();
	
	String timeStr = toAlphaNumeric(timestamp);
	
	if(!includeRandom) {
		
		return String.format(SIMPLE_RECORD_ID_FORMAT, idStr,timeStr);
		
	}
	
	if(random == null) {
		
		Random r = new Random(timestamp);
		random = Math.abs(r.nextInt(10000));
		
	}
	
	String rStr = toAlphaNumeric(random);
	
	return String.format(RECORD_ID_FORMAT, idStr,timeStr);
	
}


public static String toAlphaNumeric(long num) {
	
	return toAlphaNumeric(num,CHAR_SET);
}

public static String toAlphaNumeric(long num,char[] charSet) {
	
	int NUM_BASE = charSet.length;
	String code = "";
	
	if(num < 0)
		return null;
	
	while(true) {
		
		int reminder = (int)(num % NUM_BASE);
		num = num / NUM_BASE;
		code = charSet[reminder] + code;
		
		if(num ==0 )
			break;
		
	}
	
	return code;
	
}

}
