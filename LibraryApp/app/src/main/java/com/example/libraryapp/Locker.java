package com.example.libraryapp;

public class Locker {
    private String id;
    private String number;
    private boolean isRented;
    private String rentedBy;

    public Locker() {}

    public Locker(String id, String number, boolean isRented, String rentedBy) {
        this.id = id;
        this.number = number;
        this.isRented = isRented;
        this.rentedBy = rentedBy;
    }

    public String getId() { return id; }
    public String getNumber() { return number; }
    public boolean getIsRented() { return isRented; }
    public String getRentedBy() { return rentedBy; }
}
