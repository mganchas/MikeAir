package com.example.model.flights.api

import java.io.Serializable

data class Flight(
    var faresLeft : Int?,
    var flightKey : String?,
    var infantsLeft : Int?,
    var regularFare : RegularFare?,
    var segments : List<Segment>?,
    var flightNumber : String?,
    var time : List<String>?,
    var timeUTC : List<String>?,
    var duration : String?
) : Serializable