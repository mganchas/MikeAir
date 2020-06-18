package com.example.api.abstractions

interface WebApiService {


    fun getContentRaw(url : String) : String
    fun <T> getContentAs(url : String) : T
    fun post(url : String, parameters : HashMap<String, String>)
}