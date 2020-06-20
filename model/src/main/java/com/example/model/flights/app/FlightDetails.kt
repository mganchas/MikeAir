package com.example.model.flights.app

import java.io.Serializable

data class FlightDetails(
    var flightNumber : String,
    var origin : String,
    var destination : String,
    var infantsLeft : Int,
    var fareClass : String,
    var discountInPercent : Double
) : Serializable