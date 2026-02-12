package com.example.libraryapp;

public class Book {
    private String id;
    private String title;
    private String author;
    private String status;

    // Empty constructor required for Firestore
    public Book() {}

    public Book(String id, String title, String author, String status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getStatus() { return status; }
}
