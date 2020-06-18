package com.example.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WebApiRetrofit
{
    companion object {
        val instance : Retrofit by lazy {
            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}