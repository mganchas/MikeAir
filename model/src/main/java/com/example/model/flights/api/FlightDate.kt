package com.example.model.flights.api

import java.io.Serializable

data class FlightDate(
    var dateOut: String?,
    var flights: List<Flight>
) : Serializable