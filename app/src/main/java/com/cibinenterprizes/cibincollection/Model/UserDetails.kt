package com.cibinenterprizes.cibincollection.Model

class UserDetails {
    var Username: String? = null
    var StoreName: String? = null
    var Mobile: String? = null
    var Lantitude: String? = null
    var Longitude: String? = null
    var TermsAndConditions: String? = null
    var StoreId: String? = null
    var UID: String? = null
    var TotalCollected: String? = null
    var TotalAmount: String? = null

    constructor(){

    }

    constructor(
        Username: String?,
        StoreName: String?,
        Mobile: String?,
        Lantitude: String?,
        Longitude: String?,
        TermsAndConditions: String?, StoreId: String, UID: String?, TotalCollected: String, TotalAmount: String?
    ) {
        this.Username = Username
        this.StoreName = StoreName
        this.Mobile = Mobile
        this.Lantitude = Lantitude
        this.Longitude = Longitude
        this.TermsAndConditions = TermsAndConditions
        this.StoreId = StoreId
        this.UID = UID
        this.TotalCollected = TotalCollected
        this.TotalAmount = TotalAmount
    }
}