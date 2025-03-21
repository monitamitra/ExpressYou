package com.example.expressyou

import android.net.Network
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class RecipeViewModel : ViewModel() {

    private val openAIService = RetrofitInstance.openAIService
    private val _recipeResult = MutableLiveData<NetworkResponse<CoffeeRecipe>>()
    val recipeResult: LiveData<NetworkResponse<CoffeeRecipe>> get() = _recipeResult

    private val _imageResult = MutableLiveData<NetworkResponse<String>>()
    val imageResult: LiveData<NetworkResponse<String>> get() = _imageResult


    fun generateCoffeeRecipe(
        mood: String, sweetness: String, milkType: String,
        dietaryRestrictions: String, weatherOverview: String
    ) {

       val selectedMilkType = if (milkType.isEmpty()) "No Milk" else milkType

        val prompt = """ Generate a coffee recipe based on the following preferences:
            - Mood: $mood
            - Sweetness Level: $sweetness
            - Milk Type: $selectedMilkType
            - Dietary Restrictions: $dietaryRestrictions
            - Weather: $weatherOverview

            Format the response in JSON with the following structure:

            {
              "name": "Coffee Name",
              "ingredients": [
                {"name": "Ingredient 1", "amount": "Amount"},
                {"name": "Ingredient 2", "amount": "Amount"}
              ],
              "instructions": [
                "Step 1",
                "Step 2",
                "Step 3"
              ], 
              "weather": "Single-word weather descriptor based on the weather overview 
              (e.g. Sunny, Rainy, Breezy)" 
            }

            Ensure the response is concise, follows this JSON structure, and that the coffee recipe is unique and creative based on the given inputs.
        """.trimIndent()

        val apiKey = "Bearer ${BuildConfig.OPENAI_API_KEY}"
        val request = RecipeRequest(
            messages = listOf(
                Message(role = "system", content = "You are a personal barista."),
                Message(role = "user", content = prompt)
            )
        )

        viewModelScope.launch {
            _recipeResult.value = NetworkResponse.Loading

            try {
                val recipeResponse = openAIService.generateRecipe(apiKey, request)
                if (recipeResponse.isSuccessful) {

                    val body = recipeResponse.body()
                    if (body != null && body.choices.isNotEmpty()) {
                        val jsonResponse = JSONObject(body.choices[0].message.content)
                        Log.i("RESPONSE: ", body.choices[0].message.content)
                        val coffeeRecipe = parseRecipeResults(
                            jsonResponse, mood, sweetness, dietaryRestrictions,
                            selectedMilkType
                        )

                        var imageUrl = generateRecipeImage(coffeeRecipe.name)

                        if (imageUrl.isEmpty()) {
                            imageUrl = "https://www.browneyedbaker.com/wp-content/uploads/2021/06/iced-coffee-8-square.jpg"
                        }

                        val updatedRecipe = coffeeRecipe.copy(imageUrl = imageUrl)
                        _recipeResult.value = NetworkResponse.Success(updatedRecipe)
                    }
                } else {
                    _recipeResult.value = NetworkResponse.Error("Failed to generate recipe :(")
                }

            } catch (e: Exception) {
                _recipeResult.value = NetworkResponse.Error("Failed to generate recipe :(")
            }

        }
    }

    private fun parseRecipeResults(
        coffeeRecipeJSON: JSONObject, mood: String, sweetness: String, dietaryRestrictions: String,
        milkType: String
    ): CoffeeRecipe {

        Log.i("parseRecipeResults: ", "HERE :|")

        // ingredients
        val ingredientsArray = coffeeRecipeJSON.getJSONArray("ingredients")
        Log.i("parseRecipeResults: ", ingredientsArray.join(", "))
        val ingredients = mutableListOf<Ingredient>()
        for (i in 0 until ingredientsArray.length()) {
            val item = ingredientsArray.getJSONObject(i)
            ingredients.add(Ingredient(item.getString("name"), item.getString("amount")))
        }

        // instructions
        val instructionsArray = coffeeRecipeJSON.getJSONArray("instructions")
        val instructions = mutableListOf<String>()
        for (i in 0 until instructionsArray.length()) {
            instructions.add(instructionsArray.getString(i))
        }


        return CoffeeRecipe(
            name = coffeeRecipeJSON.getString("name"),
            ingredients = ingredients,
            instructions = instructions,
            imageUrl = "https://www.allrecipes.com/thmb/LgtetzzQWH3GMxFISSii84XEAB8=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/258686-IcedCaramelMacchiato-ddmps-4x3-104704-2effb74f7d504b8aa5fbd52204d0e2e5.jpg",
            mood = mood,
            weather = coffeeRecipeJSON.getString("weather"),
            sweetness = sweetness,
            milkType = milkType,
            dietaryRestrictions = dietaryRestrictions
        )
    }

    fun generateSurpriseCoffeeRecipe(dietaryRestrictions: String) {
        Log.i("OMG you surprised me...", dietaryRestrictions)
    }

    private suspend fun generateRecipeImage(coffeeName: String): String {
        val apiKey = "Bearer ${BuildConfig.OPENAI_API_KEY}"
        val request =
            ImageRequest(prompt = "A high-quality, professional photograph of a beautifully crafted coffee drink in a cup, named '$coffeeName'. The coffee has rich textures, creamy froth, and is visually appealing. Shot in a cozy café setting.")

        return try {
            val response = openAIService.generateImage(apiKey, request)
            if (response.isSuccessful) {
                val imageUrl = response.body()?.data?.get(0)?.url ?: ""
                Log.i("AI Image", "Generated Image URL: $imageUrl")
                imageUrl
            } else {
                Log.i("AI Image", "FAILED TO GENERATE IMAGE :(")
                ""
            }

        } catch(e: Exception) {
            Log.i("AI Image", "Image generation error: ${e.message}")
            ""
        }
    }

}