package com.example.model.flights

data class Fare(
    var type : String?,
    var amount : Double?,
    var count : Int?,
    var hasDiscount : Boolean?,
    var publishedFare : Double?,
    var discountInPercent : Int?,
    var hasPromoDiscount : Boolean?
)