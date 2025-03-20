package com.example.expressyou

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// request class for recipe text
data class RecipeRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

// response for recipe text
data class RecipeResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class ImageRequest(
    val model: String = "dall-e-2",
    val prompt: String,
    val size: String = "256x256"
)

data class ImageGenerationResponse(
    val data: List<ImageData>
)

data class ImageData(
    val url: String
)

interface OpenAIService {

    @POST("v1/chat/completions")
    suspend fun generateRecipe(
        @Header("Authorization") authorization: String,
        @Body request: RecipeRequest
    ): Response<RecipeResponse>


    @POST("v1/images/generations")
    suspend fun generateImage(
        @Header("Authorization") apiKey: String,
        @Body request: ImageRequest
    ): Response<ImageGenerationResponse>

}