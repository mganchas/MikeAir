package com.example.model.flights.api

import java.io.Serializable

data class Segment(
    var segmentNr : Int?,
    var origin : String?,
    var destination : String?,
    var flightNumber : String?,
    var time : List<String>?,
    var timeUTC : List<String>?,
    var duration : String?
) : Serializable