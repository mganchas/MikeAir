package com.example.model.airports.api

data class Station(
    var code : String?,
    var name : String?,
    var alternateName : String?,
    var alias : List<String>?,
    var countryCode : String?,
    var countryName : String?,
    var countryGroupCode : String?,
    var countryGroupName : String?,
    var timeZoneCode : String?,
    var latitude : String?,
    var longitude : String?,
    var mobileBoardingPass : Boolean?,
    var markets : List<Market>?,
    var notices : String?
)