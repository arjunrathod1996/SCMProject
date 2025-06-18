package com.SCM.insight;

import java.util.List;

import com.SCM.entities.Customer;

public class CartBillSummaryInsight {
	
	public static class BaseStat{
		private String startDate;
		private String endDate;
		private Integer entryCount;
		public String getStartDate() {
			return startDate;
		}
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public Integer getEntryCount() {
			return entryCount;
		}
		public void setEntryCount(Integer entryCount) {
			this.entryCount = entryCount;
		}
	}
	
	public static class CartItemBillOverView {
		
		Integer totalCount;
		Integer totalCompletedStatus;
		Integer totalpendingStatus;
		Integer totalCancelledStatus;
		Double  totalCompletedStatusAmount;
		Double totalPendingStatusAmount;
		Double totalCancelledStatusAmount;
		
		public Integer getTotalCount() {
			return totalCount;
		}
		public void setTotalCount(Integer totalCount) {
			this.totalCount = totalCount;
		}
		public Integer getTotalCompletedStatus() {
			return totalCompletedStatus;
		}
		public void setTotalCompletedStatus(Integer totalCompletedStatus) {
			this.totalCompletedStatus = totalCompletedStatus;
		}
		public Integer getTotalpendingStatus() {
			return totalpendingStatus;
		}
		public void setTotalpendingStatus(Integer totalpendingStatus) {
			this.totalpendingStatus = totalpendingStatus;
		}
		public Integer getTotalCancelledStatus() {
			return totalCancelledStatus;
		}
		public void setTotalCancelledStatus(Integer totalCancelledStatus) {
			this.totalCancelledStatus = totalCancelledStatus;
		}
		public Double getTotalCompletedStatusAmount() {
			return totalCompletedStatusAmount;
		}
		public void setTotalCompletedStatusAmount(Double totalCompletedStatusAmount) {
			this.totalCompletedStatusAmount = totalCompletedStatusAmount;
		}
		public Double getTotalPendingStatusAmount() {
			return totalPendingStatusAmount;
		}
		public void setTotalPendingStatusAmount(Double totalPendingStatusAmount) {
			this.totalPendingStatusAmount = totalPendingStatusAmount;
		}
		public Double getTotalCancelledStatusAmount() {
			return totalCancelledStatusAmount;
		}
		public void setTotalCancelledStatusAmount(Double totalCancelledStatusAmount) {
			this.totalCancelledStatusAmount = totalCancelledStatusAmount;
		}
		@Override
		public String toString() {
			return "CartItemBillOverView [totalCount=" + totalCount + ", totalCompletedStatus=" + totalCompletedStatus
					+ ", totalpendingStatus=" + totalpendingStatus + ", totalCancelledStatus=" + totalCancelledStatus
					+ ", totalCompletedStatusAmount=" + totalCompletedStatusAmount + ", totalPendingStatusAmount="
					+ totalPendingStatusAmount + ", totalCancelledStatusAmount=" + totalCancelledStatusAmount + "]";
		}
		
	}
	
	
	public static class CustomerRankingStat extends BaseStat{
		
		List<CustomerRankingEntry> entries;

		public List<CustomerRankingEntry> getEntries() {
			return entries;
		}

		public void setEntries(List<CustomerRankingEntry> entries) {
			this.entries = entries;
		}

		@Override
		public String toString() {
			return "CustomerRankingStat [entries=" + entries + ", getEntries()=" + getEntries() + ", getStartDate()="
					+ getStartDate() + ", getEndDate()=" + getEndDate() + ", getEntryCount()=" + getEntryCount()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
		
	}
	
	public static class CustomerRankingEntry{
		
		Customer customer;
		Integer noOfPurchase;
		Float amount;
		public Customer getCustomer() {
			return customer;
		}
		public void setCustomer(Customer customer) {
			this.customer = customer;
		}
		public Integer getNoOfPurchase() {
			return noOfPurchase;
		}
		public void setNoOfPurchase(Integer noOfPurchase) {
			this.noOfPurchase = noOfPurchase;
		}
		public Float getAmount() {
			return amount;
		}
		public void setAmount(Float amount) {
			this.amount = amount;
		}
		@Override
		public String toString() {
			return "CustomerRankingEntry [customer=" + customer + ", noOfPurchase=" + noOfPurchase + ", amount="
					+ amount + "]";
		}
	}
}
