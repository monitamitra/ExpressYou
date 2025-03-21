package com.example.expressyou

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val OPENAIBASEURL = "https://api.openai.com/"

    private fun getOPENAIInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(OPENAIBASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val openAIService : OpenAIService = getOPENAIInstance().create(OpenAIService::class.java)


    private const val WEATHERBASEURL = "https://api.openweathermap.org/"

    private fun getWeatherInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHERBASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherService : WeatherService = getWeatherInstance().create(WeatherService::class.java)

}