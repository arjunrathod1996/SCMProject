package com.SCM.entities;

public class Summary {
    private String title;
    private String subTitle;
    private String styleClass;
    private String icon;
    private String value;
    private String activeClass; // new field for the active state
    private String url;
    
    public Summary(String title, String subTitle, String styleClass, String icon, String value, String url) {
		this.title = title;
		this.subTitle = subTitle;
		this.styleClass = styleClass;
		this.icon = icon;
		this.value = value;
		this.url = url;
	}
    
	public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public String getIcon() {
        return icon;
    }

    public String getValue() {
        return value;
    }

    public String getUrl() {
        return url;
    }

	public String getActiveClass() {
		return activeClass;
	}

	private boolean active;

    // Constructors, getters, setters, etc.

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}
