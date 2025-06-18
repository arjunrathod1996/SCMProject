package com.SCM.chartandFile;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ChartService {
	
	private final Integer LESS_FREQUENT_CAP = 2;
	
	public static final String COLOR_BLUE = "#3e95cd";
	public static final String COLOR_VIOLET = "#8e5ea2";
	public static final String COLOR_GREEN = "#3cba9f";
	public static final String COLOR_PINK = "#e8c3b9";
	public static final String COLOR_RED = "#c45850";
	public static final String COLOR_LIGHT_GREEN = "#5cb85c";
	public static final String COLOR_WARNING = "#EC971F";
	
	public Integer[] parse(String[] intervals,List<Object[]> data) {
		
		Integer[]  result = new Integer[intervals.length];
		
		Map<String, Integer> valueMap = new HashMap<String,Integer>();
		
		for(int i = 0; i<result.length; i++) 
			result[i] = 0;
			
		if(data ==  null) {
			return result;
		}
		
		for(Object[] record : data) {
			valueMap.put((String) record[0], ((BigInteger) record[1]).intValue());
		}
		
		for(int i = 0; i <intervals.length; i++) {
			result[i] = valueMap.getOrDefault(intervals[i], 0);
		}
			
		return result;
		
	}
		
	public Float[] parseFloat(String[] intervals, List<Object[]> data) {
	    Float[] result = new Float[intervals.length];
	    Map<String, Float> valueMap = new HashMap<>();

	    for (int i = 0; i < result.length; i++) {
	        result[i] = 0.0f;
	    }

	    System.out.println("Data: " + data);

	    if (data == null) {
	        return result;
	    }
	    
	    for (Object[] record : data) {
	        String date = (String) record[0];
	        Number value = (Number) record[1];

	        // Print values to debug
	        System.out.println("Record - Date: " + date + ", Value: " + value);

	        // Parse and format the date to match intervals
	        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM,yyyy", Locale.ENGLISH);
	        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM, yyyy", Locale.ENGLISH);

	        try {
	            Date parsedDate = inputFormat.parse(date);
	            String formattedDate = outputFormat.format(parsedDate);

	            if (value != null) {
	                valueMap.put(formattedDate, value.floatValue());
	            } else {
	                // Handle the case where the value is null (perhaps set a default value)
	                valueMap.put(formattedDate, 0.0f); // Set a default value or handle it according to your logic
	            }
	        } catch (ParseException e) {
	            e.printStackTrace(); // Handle parsing exception if necessary
	        }
	    }


	    for (int i = 0; i < intervals.length; i++) {
	        String interval = intervals[i];
	        Float resultValue = valueMap.getOrDefault(interval, 0.0f);

	        // Print values to debug
	        System.out.println("Interval: " + interval + ", Result Value: " + resultValue);

	        result[i] = resultValue;
	    }

	    return result;
	}

	
	public String[] getIntervals1(int count) {
		
		String FORMAT = "MMM, YYYY";
		SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
		String[] months = new String[count];
		Calendar cal = Calendar.getInstance();
		
		for(int i=0; i<count; i++) {
			
			months[i] = dateFormat.format(cal.getTime());
			cal.add(Calendar.MONTH, -1);
			
		}
		
		return months;
		
		
	}
	
	public static String[] getIntervals(int count) {
	    String FORMAT = "MMM, yyyy";  // Change format to display month and year
	    SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
	    String[] months = new String[count];
	    Calendar cal = Calendar.getInstance();

	    // Set the calendar to the current month and year
	    int currentMonth = cal.get(Calendar.MONTH);
	    int currentYear = cal.get(Calendar.YEAR);

	    // Set the calendar to the most recent month based on the count provided
	    cal.set(Calendar.DAY_OF_MONTH, 1);
	    cal.set(Calendar.MONTH, currentMonth);
	    cal.set(Calendar.YEAR, currentYear);

	    for (int i = 0; i < count; i++) {
	        // Display the month and year
	        months[i] = dateFormat.format(cal.getTime());

	        // Move to the previous month
	        cal.add(Calendar.MONTH, -1);
	    }

	    return months;
	}

	
	public String[] getIntervals_(int count) {
	    String FORMAT = "MMM, yyyy";  // Change format to display month and year
	    SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);

	    String[] months = new String[6];  // Always return 6 months for the current year
	    Calendar cal = Calendar.getInstance();

	    // Set the calendar to the current month and year
	    cal.set(Calendar.DAY_OF_MONTH, 1);
	    int currentYear = cal.get(Calendar.YEAR);
	    int currentMonth = cal.get(Calendar.MONTH);

	    for (int i = 0; i < count; i++) {
	        // Display the month and year
	        months[i] = dateFormat.format(cal.getTime());

	        // Move to the previous month
	        cal.add(Calendar.MONTH, -1);

	        // Stop if we reach the start of the current year
	        if (cal.get(Calendar.YEAR) < currentYear || (cal.get(Calendar.YEAR) == currentYear && cal.get(Calendar.MONTH) < currentMonth))
	            break;
	    }

	    return months;
	}
	
	
	

	
	public Object[][] parse1(String[] intervals, List<Object[]> data) {
	    Object[][] result = new Object[intervals.length][2];
	    Map<String, Object[]> valueMap = new HashMap<>();

	    for (int i = 0; i < result.length; i++) {
	        result[i][0] = 0;
	        result[i][1] = 0.0f;
	        valueMap.put(intervals[i], result[i]);
	    }

	    if (data != null) {
	        for (Object[] record : data) {
	            String date = (String) record[0];
	            int count = ((BigInteger) record[1]).intValue();
	            float avg = ((Number) record[2]).floatValue();
	            Object[] values = valueMap.get(date);
	            if (values != null) {
	                values[0] = count;
	                values[1] = avg;
	            }
	        }
	    }

	    return result;
	}
	
	public Object[][] parse2(String[] intervals, List<Object[]> data) {
	    Object[][] result = new Object[intervals.length][2];
	    Map<String, Object[]> valueMap = new HashMap<>();

	    for (int i = 0; i < result.length; i++) {
	        result[i][0] = 0.0f; // Initialize as Long
	        result[i][1] = 0.0f;
	        valueMap.put(intervals[i], result[i]);
	    }

	    if (data != null) {
	        for (Object[] record : data) {
	            String date = (String) record[0];
	            Object count = record[1];
	            Object avg = record[2]; // Change here, using Object for avg as well
	            Object[] values = valueMap.get(date);
	            if (values != null) {
	                values[0] = count;
	                values[1] = avg;
	            }
	        }
	    }

	    return result;
	}
}




	



