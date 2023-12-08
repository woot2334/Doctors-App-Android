package com.example.quadcare.NetworkCalls

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object ApiUtilities {
    private val BASE_URL = "https://quadcare.azurewebsites.net/"

     // https://localhost:7294/api/appointment/GetAllAppointments

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }
}