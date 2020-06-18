package com.example.model.flights

data class RegularFare(
    var fareKey : String?,
    var fareClass : String?,
    var fares : List<Fare>?
)