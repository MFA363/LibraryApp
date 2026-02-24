package com.example.libraryapp20

import java.util.Date


class Report {
    var userId: String? = null
        private set
    var userName: String? = null
        private set
    var issue: String? = null
        private set
    var timestamp: Date? = null
        private set

    constructor() // Required for Firebase

    constructor(userId: String?, userName: String?, issue: String?, timestamp: Date?) {
        this.userId = userId
        this.userName = userName
        this.issue = issue
        this.timestamp = timestamp
    }
}