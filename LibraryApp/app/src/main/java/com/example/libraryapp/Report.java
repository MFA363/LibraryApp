package com.example.libraryapp;

import java.util.Date;

public class Report {
    private String userId;
    private String userName;
    private String issue;
    private Date timestamp;

    public Report() {} // Required for Firebase

    public Report(String userId, String userName, String issue, Date timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.issue = issue;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getIssue() { return issue; }
    public Date getTimestamp() { return timestamp; }
}
