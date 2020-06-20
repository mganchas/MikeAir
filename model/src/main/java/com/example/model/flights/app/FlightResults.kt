package com.example.model.flights.app

import java.io.Serializable

data class FlightResults(
    var origin : String,
    var destination : String,
    var currency : String,
    var results : List<FlightResult>
) : Serializable