package com.example.model.flights.api

import java.io.Serializable

data class Trip(
    var origin: String?,
    var originName: String?,
    var destination: String?,
    var destinationName: String?,
    var dates : List<FlightDate>?
) : Serializable