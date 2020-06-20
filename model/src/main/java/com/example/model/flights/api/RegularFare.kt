package com.example.model.flights.api

import java.io.Serializable

data class RegularFare(
    var fareKey : String?,
    var fareClass : String?,
    var fares : List<Fare>?
) : Serializable