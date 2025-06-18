package com.SCM.cartItem;

public class CartItemDto {
	
	private Long itemId;
	private String shortLink;
	private String staffLink;
	private Double itemAmount;
	public Long getItemId() {
		return itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	public String getShortLink() {
		return shortLink;
	}
	public void setShortLink(String shortLink) {
		this.shortLink = shortLink;
	}
	public String getStaffLink() {
		return staffLink;
	}
	public void setStaffLink(String staffLink) {
		this.staffLink = staffLink;
	}
	public Double getItemAmount() {
		return itemAmount;
	}
	public void setItemAmount(Double itemAmount) {
		this.itemAmount = itemAmount;
	}

}
