package com.SCM.helpers;

public class ExceptionMessage {
	
	private String message;
	private boolean success;
	
	public ExceptionMessage(String message, boolean success) {
		super();
		this.message = message;
		this.success = success;
	}

	public ExceptionMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}