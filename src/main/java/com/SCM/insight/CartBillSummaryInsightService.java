package com.SCM.insight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.SCM.cartItem.CartItemRepository;
import com.SCM.chartandFile.ChartData;
import com.SCM.chartandFile.ChartData.DataSet;
import com.SCM.chartandFile.ChartService;
import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.Customer;
import com.SCM.insight.CartBillSummaryInsight.CartItemBillOverView;
import com.SCM.insight.CartBillSummaryInsight.CustomerRankingEntry;
import com.SCM.insight.CartBillSummaryInsight.CustomerRankingStat;
import com.SCM.repository.CustomerRepository;
import com.SCM.service.CommonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CartBillSummaryInsightService {
	
	@Autowired
	CommonService commonService;
	@Autowired
	CartItemRepository cartItemRepository;
	@Autowired
	ChartService chartService;
	@Autowired
	CustomerRepository customerRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(CartBillSummaryInsightService.class);
	
	final int MIN_ENTRY_COUNT = 5;
	final int MAX_ENTRY_COUNT = 50;
	final int GRAPH_DURATION_MONTHS = 12;
	final String DEFAULT_START_DATE = "1970-01-01";
	
	
//	public CartItemBillOverView getCartBillSummaryOverView(Business business) {
//		
//		CartItemBillOverView overView = new CartItemBillOverView();
//		List<Merchant> merchants = commonService.getMerchants(business);
//		List<Long> merchantIDs = new ArrayList<>();
//		for(Merchant m : merchants)
//			merchantIDs.add(m.getId());
//		List<Object[]> records = cartItemRepository.getCartItemStatistics(merchantIDs);
//
//        if (records == null || records.isEmpty()) {
//            throw new IllegalStateException("The query did not return any results");
//        }
//
////        Object[] stat = stats.get(0); // Assuming the query returns a single result row
////
////        Map<String, Object> statistics = new HashMap<>();
////        statistics.put("totalCount", ((Number) stat[0]).intValue());
////        statistics.put("totalCompletedStatus", ((Number) stat[1]).intValue());
////        statistics.put("totalPendingStatus", ((Number) stat[2]).intValue());
////        statistics.put("totalCancelledStatus", ((Number) stat[3]).intValue());
////        statistics.put("totalCompletedStatusAmount", ((Number) stat[4]).doubleValue());
////        statistics.put("totalPendingStatusAmount", ((Number) stat[5]).doubleValue());
////        statistics.put("totalCancelledStatusAmount", ((Number) stat[6]).doubleValue());
////
////        return statistics;
//		
//        overView.setTotalCount(0);
//        overView.setTotalCompletedStatus(0);
//        overView.setTotalpendingStatus(0);
//        overView.setTotalCancelledStatus(0);
//        overView.setTotalCompletedStatusAmount(0.0);
//        overView.setTotalPendingStatusAmount(0.0);
//        overView.setTotalCancelledStatusAmount(0.0);
//
//        for (Object[] stat : records) {
//            overView.setTotalCount(overView.getTotalCount() + ((Number) stat[0]).intValue());
//            overView.setTotalCompletedStatus(overView.getTotalCompletedStatus() + ((Number) stat[1]).intValue());
//            overView.setTotalpendingStatus(overView.getTotalpendingStatus() + ((Number) stat[2]).intValue());
//            overView.setTotalCancelledStatus(overView.getTotalCancelledStatus() + ((Number) stat[3]).intValue());
//            overView.setTotalCompletedStatusAmount(overView.getTotalCompletedStatusAmount() + ((Number) stat[4]).doubleValue());
//            overView.setTotalPendingStatusAmount(overView.getTotalPendingStatusAmount() + ((Number) stat[5]).doubleValue());
//            overView.setTotalCancelledStatusAmount(overView.getTotalCancelledStatusAmount() + ((Number) stat[6]).doubleValue());
//        }
//	
//		return overView;
//	}
	
	
	public CartItemBillOverView getCartBillSummaryOverView(Business business) {
        CartItemBillOverView overView = new CartItemBillOverView();
        
        try {
            List<Merchant> merchants = commonService.getMerchants(business);
            List<Long> merchantIDs = merchants.stream().map(Merchant::getId).collect(Collectors.toList());

            List<Object[]> records = cartItemRepository.getCartItemStatistics(merchantIDs);

            overView.setTotalCount(0);
            overView.setTotalCompletedStatus(0);
            overView.setTotalpendingStatus(0);
            overView.setTotalCancelledStatus(0);
            overView.setTotalCompletedStatusAmount(0.0);
            overView.setTotalPendingStatusAmount(0.0);
            overView.setTotalCancelledStatusAmount(0.0);

            for (Object[] stat : records) {
                overView.setTotalCount(overView.getTotalCount() + ((Number) stat[0]).intValue());
                overView.setTotalCompletedStatus(overView.getTotalCompletedStatus() + ((Number) stat[1]).intValue());
                overView.setTotalpendingStatus(overView.getTotalpendingStatus() + ((Number) stat[2]).intValue());
                overView.setTotalCancelledStatus(overView.getTotalCancelledStatus() + ((Number) stat[3]).intValue());
                overView.setTotalCompletedStatusAmount(overView.getTotalCompletedStatusAmount() + ((Number) stat[4]).doubleValue());
                overView.setTotalPendingStatusAmount(overView.getTotalPendingStatusAmount() + ((Number) stat[5]).doubleValue());
                overView.setTotalCancelledStatusAmount(overView.getTotalCancelledStatusAmount() + ((Number) stat[6]).doubleValue());
            }
        } catch (Exception e) {
            logger.error("Error fetching cart item statistics for business: {}", business.getId(), e);
        }

        return overView;
    }
	
	
	public String getBillSummaryProgramGraph(Business business) {
			
		List<Merchant> merchants = commonService.getMerchants(business);
	    List<Long> merchantIDs = new ArrayList<>();

	    for (Merchant m : merchants) {
	        merchantIDs.add(m.getId());
	    }
	   
	    Integer numberOfMonths = 6;
	    Integer duration = numberOfMonths * 31; // Assuming each month has 31 days
	    String[] intervals = chartService.getIntervals(numberOfMonths);
	    ChartData chartData = new ChartData();
	    chartData.setLabels(intervals);
	    
	 
	    List<Object[]> combinedData = cartItemRepository.getCombinedData(merchantIDs, duration);
	    
	    List<Object[]>countCart = new ArrayList<>();
	    List<Object[]> cartAmount = new ArrayList<>();
	    
	    for (Object[] row : combinedData) {
	        String date = (String) row[0];
	        
	        // Handle the casting appropriately based on the actual data types
	        Long count = ((Number) row[1]).longValue();  // Assuming COUNT_REDEEM is of type BIGINT
	        Double amount = ((Number) row[2]).doubleValue(); // Assuming REDEEM_AMOUNT is of type DECIMAL or DOUBLE
	        countCart.add(new Object[]{date, count});
	        cartAmount.add(new Object[]{date, amount});
	       
	    }
	    
	    // Prepare datasets
	    DataSet countInstances = new DataSet();
	    countInstances.setLabel("COUNT");
	    countInstances.setBackgroundColor(new String[] { ChartService.COLOR_BLUE });
	    countInstances.setBorderColor(ChartService.COLOR_BLUE); 
	    countInstances.setData(chartService.parseFloat(intervals, countCart));
    
	    DataSet redeemAmountInstances = new DataSet();
	    redeemAmountInstances.setLabel("AMOUNT");  // Change label
	    redeemAmountInstances.setBackgroundColor(new String[] { ChartService.COLOR_GREEN });
	    redeemAmountInstances.setBorderColor(ChartService.COLOR_GREEN);
	    redeemAmountInstances.setData(chartService.parseFloat(intervals, cartAmount));
	    
	    chartData.setDatasets(new DataSet[] { countInstances,redeemAmountInstances});
	    
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
	        return objectMapper.writeValueAsString(chartData);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return "Sorry";
	    }
	}

	public CustomerRankingStat getCustomerRankingStat(Business business, String startDate, String endDate, Integer entryCount) {

        CustomerRankingStat crStat = new CustomerRankingStat();

        List<Long> merchantIDs = commonService.getMerchantIDs(business);

        if (startDate == null) {
            // Set default start date to one year before the current date
            LocalDate oneYearAgo = LocalDate.now().minusYears(1);
            startDate = oneYearAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        if (endDate == null) {
            // Set default end date to the current date
            endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        if (entryCount == null || entryCount < MIN_ENTRY_COUNT || entryCount > MAX_ENTRY_COUNT)
            entryCount = MIN_ENTRY_COUNT;

        crStat.setStartDate(startDate);
        crStat.setEndDate(endDate);
        crStat.setEntryCount(entryCount);

        PageRequest pageRequest = PageRequest.of(0, entryCount);

        List<Object[]> topCustomerByActivities = cartItemRepository.findCustomerCartItemCounts(merchantIDs, startDate, endDate, pageRequest);

        Map<Customer, CustomerRankingEntry> entryMap = new LinkedHashMap<>();

        for (Object[] record : topCustomerByActivities) {
            Long customerId = (Long) record[0];
            Integer activityCount = ((Number) record[1]).intValue();
            Float amount = ((Number) record[2]).floatValue();

            // Fetch the Customer entity using the customer ID
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer != null) {
                CustomerRankingEntry entry = new CustomerRankingEntry();
                entry.setCustomer(customer);
                entry.setNoOfPurchase(activityCount);
                entry.setAmount(amount);
                entryMap.put(customer, entry);
            }
        }

        crStat.setEntries(new ArrayList<>(entryMap.values()));

        List<CustomerRankingEntry> entries = crStat.getEntries();

        return crStat;
    }
	
}
