package com.SCM.cartItem;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;

public class InvoiceExtras {
	
	Float CGSTPercentage;
	
	Float SGSTPercentage;
	
	Float IGSTPerncentage;
	
	Float extraTaxPercentagel;
	
	Float CGSTValue;
	
	Float SGSTValue;
	
	Float IGSTValue;
	
	Float extraTax;
	
	String paymentDetails;
	
	Float outstandingAmount;
	
	//Bucket bucket;
	
	String internalNote;
	
	boolean excludeTaxCal;
	
	InvoiceContent content;
	
	public static InvoiceExtras fromJSON(String jsonData) {
		
		InvoiceExtras invoiceExtras = null;
		
		try {
			
			invoiceExtras = new Gson().fromJson(jsonData, InvoiceExtras.class);
			
			
		}catch(JsonSyntaxException ex) {
			
			ex.printStackTrace();
			
		}
		
		return invoiceExtras;
		
	}
	
	public String toJSON() {
		
		return new Gson().toJson(this);
		
	}

	public Float getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(Float outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

//	public Bucket getBucket() {
//		return bucket;
//	}
//
//	public void setBucket(Bucket bucket) {
//		this.bucket = bucket;
//	}

	public String getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public String getInternalNote() {
		return internalNote;
	}

	public void setInternalNote(String internalNote) {
		this.internalNote = internalNote;
	}

	public boolean isExcludeTaxCal() {
		return excludeTaxCal;
	}

	public void setExcludeTaxCal(boolean excludeTaxCal) {
		this.excludeTaxCal = excludeTaxCal;
	}

	public Float getCGSTPercentage() {
		return CGSTPercentage;
	}

	public void setCGSTPercentage(Float cGSTPercentage) {
		CGSTPercentage = cGSTPercentage;
	}

	public Float getSGSTPercentage() {
		return SGSTPercentage;
	}

	public void setSGSTPercentage(Float sGSTPercentage) {
		SGSTPercentage = sGSTPercentage;
	}

	public Float getIGSTPerncentage() {
		return IGSTPerncentage;
	}

	public void setIGSTPerncentage(Float iGSTPerncentage) {
		IGSTPerncentage = iGSTPerncentage;
	}

	public Float getExtraTaxPercentagel() {
		return extraTaxPercentagel;
	}

	public void setExtraTaxPercentagel(Float extraTaxPercentagel) {
		this.extraTaxPercentagel = extraTaxPercentagel;
	}

	public Float getCGSTValue() {
		return CGSTValue;
	}

	public void setCGSTValue(Float cGSTValue) {
		CGSTValue = cGSTValue;
	}

	public Float getSGSTValue() {
		return SGSTValue;
	}

	public void setSGSTValue(Float sGSTValue) {
		SGSTValue = sGSTValue;
	}

	public Float getIGSTValue() {
		return IGSTValue;
	}

	public void setIGSTValue(Float iGSTValue) {
		IGSTValue = iGSTValue;
	}

	public Float getExtraTax() {
		return extraTax;
	}

	public void setExtraTax(Float extraTax) {
		this.extraTax = extraTax;
	}

	public InvoiceContent getContent() {
		return content;
	}

	public void setContent(InvoiceContent content) {
		this.content = content;
	}
	
}

