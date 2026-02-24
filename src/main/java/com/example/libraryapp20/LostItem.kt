package com.example.libraryapp20

/**
 * Model for lost & found items.
 * Keeps a no-arg constructor so Firestore can deserialize documents.
 */
class LostItem() {

    var id: String? = null
    var description: String? = null
    var dateFound: String? = null
    var locationFound: String? = null
    var status: String? = null // "Unclaimed" or "Claim Pending"

    // Full constructor for convenience when creating instances in code
    constructor(
        id: String?,
        description: String?,
        dateFound: String?,
        locationFound: String?,
        status: String?
    ) : this() {
        this.id = id
        this.description = description
        this.dateFound = dateFound
        this.locationFound = locationFound
        this.status = status
    }
}
