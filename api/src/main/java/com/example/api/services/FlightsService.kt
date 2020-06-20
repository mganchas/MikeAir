package com.example.api.services

import com.example.model.flights.api.FlightSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightsService {
    @GET("https://sit-nativeapps.ryanair.com/api/v3/Availability")
    fun getFlight(
        @Query("origin") origin : String?,
        @Query("destination") destination : String?,
        @Query("dateout") dateOut : String?,
        @Query("datein") dateIn : String?,
        @Query("flexdaysbeforeout") flexDaysBeforeOut : Int,
        @Query("flexdaysout") flexDaysOut : Int,
        @Query("flexdaysbeforein") flexDaysBeforeIn : Int,
        @Query("flexdaysin") flexDaysIn : Int,
        @Query("adt") adultCount : Int,
        @Query("teen") teenCount : Int,
        @Query("chd") childrenCount : Int,
        @Query("roundtrip") roundTrip : Boolean?,
        @Query("ToUs") toUs : String?
    ) : Call<FlightSearch>
}