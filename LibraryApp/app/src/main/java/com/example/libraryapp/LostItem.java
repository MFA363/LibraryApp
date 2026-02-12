package com.example.libraryapp;

public class LostItem {
    private String id;
    private String description;
    private String dateFound;
    private String locationFound;
    private String status; // "Unclaimed" or "Claim Pending"

    public LostItem() {} // Required for Firebase

    public LostItem(String id, String description, String dateFound, String locationFound, String status) {
        this.id = id;
        this.description = description;
        this.dateFound = dateFound;
        this.locationFound = locationFound;
        this.status = status;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public String getDateFound() { return dateFound; }
    public String getLocationFound() { return locationFound; }
    public String getStatus() { return status; }
}
