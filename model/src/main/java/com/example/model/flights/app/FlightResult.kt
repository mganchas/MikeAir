package com.example.model.flights.app

import java.io.Serializable

data class FlightResult(
    var flightDate: String,
    var flightNumber: String,
    var duration: String,
    var infantsLeft: Int,
    var farePrice: Double,
    var fareClass: String,
    var discountInPercent: Double
) : Serializable