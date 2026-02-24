package com.example.libraryapp20

/**
 * Simple Book model for use with Firestore.
 * Keeps a no-arg constructor so Firestore can deserialize documents.
 */
class Book() {

    // Fields are nullable to be safe when Firestore returns missing values.
    var id: String? = null
    var title: String? = null
    var author: String? = null
    var status: String? = null // "Available", "Reserved", "Borrowed"

    // Full constructor to create instances in code
    constructor(id: String?, title: String?, author: String?, status: String?) : this() {
        this.id = id
        this.title = title
        this.author = author
        this.status = status
    }
}
