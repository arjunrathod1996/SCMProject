package com.SCM.cartItem;

public class InvoiceContent {

	private String title = "Tax Invoice";
	private String subTitle = "Invoice";
	private String creditNote = "Credit Note";
	private String businessName;
	private String businessAddress;
	private String businessGSTIN;
	private String slingLoftGSTIN ="29ABCDEF";
	private String slingLoftAddress;
	private boolean showGstHsnCode;
	private String gstHsnCode = "998599";
	private String setupFeeLabel = "Setup Fee";
	private String serviceFeeLabel;
	
	InvoiceExtras invoiceExtras;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getBusinessAddress() {
		return businessAddress;
	}
	public void setBusinessAddress(String businessAddress) {
		this.businessAddress = businessAddress;
	}
	public String getBusinessGSTIN() {
		return businessGSTIN;
	}
	public void setBusinessGSTIN(String businessGSTIN) {
		this.businessGSTIN = businessGSTIN;
	}
	public String getSlingLoftGSTIN() {
		return slingLoftGSTIN;
	}
	public void setSlingLoftGSTIN(String slingLoftGSTIN) {
		this.slingLoftGSTIN = slingLoftGSTIN;
	}
	public String getSlingLoftAddress() {
		return slingLoftAddress;
	}
	public void setSlingLoftAddress(String slingLoftAddress) {
		this.slingLoftAddress = slingLoftAddress;
	}
	public boolean isShowGstHsnCode() {
		return showGstHsnCode;
	}
	public void setShowGstHsnCode(boolean showGstHsnCode) {
		this.showGstHsnCode = showGstHsnCode;
	}
	public String getGstHsnCode() {
		return gstHsnCode;
	}
	public void setGstHsnCode(String gstHsnCode) {
		this.gstHsnCode = gstHsnCode;
	}
	public String getSetupFeeLabel() {
		return setupFeeLabel;
	}
	public void setSetupFeeLabel(String setupFeeLabel) {
		this.setupFeeLabel = setupFeeLabel;
	}
	public String getServiceFeeLabel() {
		return serviceFeeLabel;
	}
	public void setServiceFeeLabel(String serviceFeeLabel) {
		this.serviceFeeLabel = serviceFeeLabel;
	}
	public String getCreditNote() {
		return creditNote;
	}
	public void setCreditNote(String creditNote) {
		this.creditNote = creditNote;
	}
	public InvoiceExtras getInvoiceExtras() {
		return invoiceExtras;
	}
	public void setInvoiceExtras(InvoiceExtras invoiceExtras) {
		this.invoiceExtras = invoiceExtras;
	}
	
}
