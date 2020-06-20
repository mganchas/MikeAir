package com.example.model.flights.api

import com.example.model.flights.api.Fare
import java.io.Serializable

data class RegularFare(
    var fareKey : String?,
    var fareClass : String?,
    var fares : List<Fare>?
) : Serializable