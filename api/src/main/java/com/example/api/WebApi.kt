package com.example.api

import com.example.api.services.FlightsService
import com.example.api.services.StationsService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WebApi
{
    companion object
    {
        private val apiInstance by lazy {
            Retrofit.Builder()
                .baseUrl("http://localhost/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val stationsService = apiInstance.create(StationsService::class.java)
        val flightsService = apiInstance.create(FlightsService::class.java)
    }
}