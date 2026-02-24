package com.example.libraryapp20

class Locker {
    var id: String? = null
        private set
    var number: String? = null
        private set
    var isRented: Boolean = false
        private set
    var rentedBy: String? = null
        private set

    constructor()

    constructor(id: String?, number: String?, isRented: Boolean, rentedBy: String?) {
        this.id = id
        this.number = number
        this.isRented = isRented
        this.rentedBy = rentedBy
    }
}