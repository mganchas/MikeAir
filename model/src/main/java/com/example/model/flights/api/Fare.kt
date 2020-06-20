package com.example.model.flights.api

import java.io.Serializable

data class Fare(
    var type : String?,
    var amount : Double?,
    var count : Int?,
    var hasDiscount : Boolean?,
    var publishedFare : Double?,
    var discountInPercent : Double?,
    var hasPromoDiscount : Boolean?
) : Serializable