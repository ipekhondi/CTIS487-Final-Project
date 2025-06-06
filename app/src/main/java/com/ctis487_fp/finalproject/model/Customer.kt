package com.ctis487_fp.finalproject.model

class Customer {
    // phone is a key to firebase !!!!!!!!!!
    var name: String = ""
    var email: String = ""
    var password: String = ""
    var phone : String = ""
    var regionCustomer : String = ""

    constructor(name: String, email: String, password: String, phone: String, regionCustomer: String) {
        this.name = name
        this.email = email
        this.password = password
        this.phone = phone
        this.regionCustomer = regionCustomer
    }
}