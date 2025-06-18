package com.SCM.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	
	private static final int DEFAULT_NUMBER_OF_YEARS = 10;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	public static final Integer REMAINING_DAYS = 70;
	
	public enum DateFormat {
	    
	    FORMAT_3_DATE("yyyy-MM-dd","-","2010-05-20");

	    private String format;
	    private String sep;
	    private String sample;
		private DateFormat(String format, String sep, String sample) {
			this.format = format;
			this.sep = sep;
			this.sample = sample;
		}
		public String getFormat() {
			return format;
		}
		public void setFormat(String format) {
			this.format = format;
		}
		public String getSep() {
			return sep;
		}
		public void setSep(String sep) {
			this.sep = sep;
		}
		public String getSample() {
			return sample;
		}
		public void setSample(String sample) {
			this.sample = sample;
		}

	   
	}
	
	public static Date now() {
	       return getLocalCalendar().getTime();
	    }
	 
	 public static Calendar getLocalCalendar() {
		 
		 return Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
		 
	 }
	 
	 public static String todayDate_() {
		 
		 return Utils.dateToString(Utils.now(), "ddMMyyyy");
		 
	 }
	 
	 public static Date future(Integer duration) {
		 
		 Calendar cal = getLocalCalendar();
		 
		 cal.add(Calendar.DAY_OF_YEAR, duration);
		 
		 return cal.getTime();
		 
	 }
	 
	 public static String dateToString(Date date , String format) {
		 
		 if(date == null) {
			 return "";
		 }
		 
		 SimpleDateFormat sdf = new SimpleDateFormat(format);
		 
		 return sdf.format(date);
		 
	 }
	 
	 public static String futureDateStr(Integer duration) {
		 
		 return Utils.dateToString(future(duration),"yyyy-MM-dd");
		 
	 }
	 
	 public static String twoDecimalValue(float value) {
	        DecimalFormat decimalFormat = new DecimalFormat("0.00"); // Format pattern for two decimal places
	        return decimalFormat.format(value);
	    }
	
	    
	    public static Date pastDate(Date currentDate, int days) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(currentDate);
	        calendar.add(Calendar.DAY_OF_YEAR, -days);
	        return calendar.getTime();
	    }
	    
	    public static Date getFirstDayOfMonth(Date date) {
	    	
	    	Calendar cal = getLocalCalendar();
	    	cal.setTime(date);
	    	cal.set(Calendar.DAY_OF_MONTH, 1);
	    	return cal.getTime();
	    	
	    }
	    
	    public static String[] applyTimeRangeLimit(String startDate,String endDate,Integer maxDuration) {
	    	
	    	if(maxDuration == null || maxDuration < 0)
	    		return new String[] {startDate,endDate};
	    	
	    	if(startDate == null) {
	    		
	    		startDate = Utils.dateToString(Utils.pastDate(Utils.now(),30),"yyyy-MM-dd");
	    		endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
	    	}
	    	
	    	Date maxStartDt = Utils.getFirstDayOfMonth(Utils.pastDate(Utils.now(), maxDuration));
	    	Date maxEndDt = Utils.now();
	    	Date startDt = startDate == null ? maxStartDt : stringToDate(startDate,DateFormat.FORMAT_3_DATE.getFormat());
	    	Date endDt = endDate == null ? maxEndDt : stringToDate(endDate,DateFormat.FORMAT_3_DATE.getFormat());
	    
//	    	 Date startDt = startDate == null ? maxStartDt : stringToDate(startDate, "yyyy-MM-dd");
//	         Date endDt = endDate == null ? maxEndDt : stringToDate(endDate, "yyyy-MM-dd");
	    	
	    	if(startDt.before(maxStartDt))
	    		startDt = maxStartDt;
	    	
	    	if(endDt.before(startDt))
	    		endDt = maxEndDt;
	    	
	    	startDate = Utils.dateToString(startDt, "yyyy-MM-dd");
	    	endDate = Utils.dateToString(endDt, "yyyy-MM-dd");
	    	
	    	return new String[] {startDate , endDate};
 	    	
	    }
	    
	    public static Date stringToDate(String dateString, String format) {
	        try {
	            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
	            return dateFormat.parse(dateString);
	        } catch (ParseException e) {
	            // Handle the parsing exception if needed
	            return null;
	        }
	    }
	    
	    public static Integer verificationCode() {
	    	
	    	Random random = new Random();
	    	return (1000 + random.nextInt());
	    	
	    }
	    
	    
	    public static String getFinancialYear() {
	    	
	    	List<Integer> years = getFinancialYears();
	    	
	    	// e.g 2020-2021
	    	
	    	String yearRange = ("" + years.get(0)) + "-" + (""+ years.get(1)).substring(2);
	    	
	    	 //String yearRange = String.format("%d,-%02d", years.get(0), years.get(1) % 100);
	    	
	        return yearRange;
	    }
	    
		public static List<Integer> getFinancialYears() {
			
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
			int currentYear = cal.get(Calendar.YEAR);
			int currentMonth = cal.get(Calendar.MONTH);
			int nextYear = 0;
			
			if(currentMonth < 3) { // Before April
				nextYear = currentYear;
				currentYear = nextYear - 1;
			}else {
				nextYear = currentYear + 1;
			}
			
			return Arrays.asList(currentYear,nextYear);
		}

		public static String simpleDate(Date date) {
			if(date == null)
				return null;
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
			
			return sdf.format(date);
		}
		
		public static String twoDecimalValue(Float value) {
			return value != null ? String.format("%1$.2f", value) : "";
	    }
		
		public static String toJSON(Object object) {
	        try {
	            return objectMapper.writeValueAsString(object);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null; // Handle the error condition appropriately
	        }
	    }
		
}



