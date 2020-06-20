package com.example.api.services

import com.example.model.airports.app.StationList
import retrofit2.Call
import retrofit2.http.GET

interface StationsService
{
    @GET("https://tripstest.ryanair.com/static/stations.json")
    fun getStations() : Call<StationList>
}