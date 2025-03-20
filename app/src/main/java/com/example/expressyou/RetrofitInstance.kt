package com.example.expressyou

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASEURL = "https://api.openai.com/"

    private fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val openAIService : OpenAIService = getInstance().create(OpenAIService::class.java)

}