package com.example.expressyou

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


data class WeatherOverviewResponse (
    val lat: Double,
    val lon: Double,
    val tz: String,
    val date: String,
    val units: String,
    val weather_overview: String
)

interface WeatherService {

    @GET("data/3.0/onecall/overview")
    suspend fun getWeatherSummary(
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherOverviewResponse>
}