package com.SCM.enums;

public enum Category {
    // Clothing categories
    CLOTHING("Clothing"),
    ACCESSORIES("Accessories"),
    FOOTWEAR("Footwear"),
    JEWELRY("Jewelry"),

    // Restaurant categories
    RESTAURANT("Restaurant"),
    CAFE("Cafe"),
    FAST_FOOD("Fast Food"),
    FINE_DINING("Fine Dining"),
    CASUAL_DINING("Casual Dining"),

    // Technology categories
    ELECTRONICS("Electronics"),
    COMPUTERS("Computers"),
    MOBILE_PHONES("Mobile Phones"),
    SOFTWARE("Software"),
    
    // Home categories
    FURNITURE("Furniture"),
    APPLIANCES("Appliances"),
    HOME_DECOR("Home Decor"),
    GARDENING("Gardening"),

    // Health and Beauty categories
    HEALTH("Health"),
    BEAUTY("Beauty"),
    FITNESS("Fitness"),
    WELLNESS("Wellness"),
    
    // Entertainment categories
    MOVIES("Movies"),
    MUSIC("Music"),
    GAMES("Games"),
    BOOKS("Books"),
    
    // Travel categories
    FLIGHTS("Flights"),
    HOTELS("Hotels"),
    CAR_RENTALS("Car Rentals"),
    TOURS("Tours"),

    // Services categories
    EDUCATION("Education"),
    FINANCE("Finance"),
    LEGAL("Legal"),
    CONSULTING("Consulting"),

    // Miscellaneous categories
    FOOD("Food"),
    BEVERAGES("Beverages"),
    SPORTS("Sports"),
    PETS("Pets");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

