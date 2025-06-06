package com.ctis487_fp.finalproject.model

class Sales {
    // phone is a key to firebase !!!!!!!!!!
    var name: String = ""
    var email: String = ""
    var password: String = ""
    var phone : String = ""
    var region: String = ""

    constructor(name: String, email: String, password: String, phone: String, region: String) {
     this.name = name
     this.email = email
     this.password = password
     this.phone = phone
     this.region = region
    }
}