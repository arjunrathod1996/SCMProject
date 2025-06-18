package com.SCM.insight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.insight.CartBillSummaryInsight.CartItemBillOverView;
import com.SCM.insight.CartBillSummaryInsight.CustomerRankingStat;
import com.SCM.role.Role.RoleType;
import com.SCM.service.CommonService;

@RequestMapping("/user/dashboard/bill/insight")
@Controller
public class CartBillSummaryInsightController {
	
	@Autowired
	CommonService commonService;
	@Autowired
	CartBillSummaryInsightService billSummaryInsightService;
	
	private static final Logger logger = LoggerFactory.getLogger(CartBillSummaryInsightController.class);
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getActivityOverviewAndTraceGraph(Model model,
			@RequestParam(value = "startDate",required = false)String startDate,
			@RequestParam(value = "endDate",required = false)String endDate,
			@RequestParam(value = "entryCount",required = false)Integer entryCount,
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWithh) {
		
		System.out.println("startDate : "  +startDate);
		System.out.println("startDate : "  +endDate);
		System.out.println("entryCoun : "  +entryCount);
		
		Business business = null;
		 User user = commonService.getLoggedInUser();
		
		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN) {
			business = user.getBusiness();
		}
		
		if(user.getRole().getName() == RoleType.ROLE_ADMIN) {
			business = user.getMerchant().getBusiness();
		}
		
	    try {
	       
	        // Get CartItemBillOverView
	        CartItemBillOverView cartItemBillOverView = billSummaryInsightService.getCartBillSummaryOverView(business);
	        model.addAttribute("cartItemBillOverView", cartItemBillOverView);
	        // Get TraceGraph
	        String traceGraph = billSummaryInsightService.getBillSummaryProgramGraph(business);
	        model.addAttribute("traceGraph", traceGraph);
	        // Get Top Customer
	        CustomerRankingStat ctStat = billSummaryInsightService.getCustomerRankingStat(business, startDate, endDate, entryCount);
	        model.addAttribute("ctStat", ctStat);
	        System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : " + ctStat);
	        
	    } catch (Exception e) {
	        logger.error("Error in getActivityOverviewAndTraceGraph", e);
	    }
	    
	    
	    
//	    if ("XMLHttpRequest".equals(requestedWithh)) {
//            return "user/bill_insight_fragments :: customer_wise_rank_table_rows";
//        }
	    
	    return "user/bill_insight_fragments"; // :: fr_overview
	}

	


}
