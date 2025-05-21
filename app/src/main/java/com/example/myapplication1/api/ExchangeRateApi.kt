package com.example.myapplication1.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {
    @GET("convert")
    fun convertCurrency(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double,
        @Query("access_key") key: String
    ): Call<ExchangeRateResponse>
}