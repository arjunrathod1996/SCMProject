package com.SCM.photo;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;

public class PhotoExtras {
	
	String title;
	
	String link;
	
	String price;
	
	String description;

	
	public static PhotoExtras fromJSON(String jsonData) {
		PhotoExtras photoExtras = null;
		
		try {
			photoExtras = new Gson().fromJson(jsonData, PhotoExtras.class);
		}catch(JsonSyntaxException ex) {
			ex.printStackTrace();
		}
		
		return photoExtras;
	}
	
	public String toJSON() {
		return new Gson().toJson(this);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
