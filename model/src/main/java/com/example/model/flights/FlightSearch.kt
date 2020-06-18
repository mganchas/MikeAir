package com.example.model.flights

data class FlightSearch(
    var termsOfUse: String?,
    var currency: String?,
    var currPrecision: String?,
    var trips: List<Trip>?,
    var serverTimeUTC : String?
)