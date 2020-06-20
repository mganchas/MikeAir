package com.example.model.flights.api

import com.example.model.flights.api.Flight
import java.io.Serializable

data class FlightDate(
    var dateOut: String?,
    var flights: List<Flight>
) : Serializable