package com.cibinenterprizes.cibincollection.Model

class ReportDetails {
    var Date: String? = null
    var Collected: String? = null
    var Amount: String? = null

    constructor(){

    }
    constructor(Date: String?, Collected: String?, Amount: String?) {
        this.Date = Date
        this.Collected = Collected
        this.Amount = Amount
    }
}