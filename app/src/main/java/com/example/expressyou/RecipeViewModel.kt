package com.example.expressyou

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.UUID

class RecipeViewModel : ViewModel() {

    private val openAIService = RetrofitInstance.openAIService
    private val _recipeResult = MutableLiveData<NetworkResponse<CoffeeRecipe>>()
    val recipeResult: LiveData<NetworkResponse<CoffeeRecipe>> get() = _recipeResult

    private val _imageResult = MutableLiveData<NetworkResponse<String>>()
    val imageResult: LiveData<NetworkResponse<String>> get() = _imageResult

    private val db = FirebaseFirestore.getInstance()
    private val _favoriteRecipes = MutableStateFlow<List<CoffeeRecipe>>(emptyList())
    val favoriteRecipes: StateFlow<List<CoffeeRecipe>> = _favoriteRecipes

    private val _showDialog = MutableLiveData(false)
    val showDialog: LiveData<Boolean> get() = _showDialog


    fun updateShowDialogState(state: Boolean) {
        _showDialog.value = state
    }

    fun generateCoffeeRecipe(
        mood: String, sweetness: String, milkType: String,
        dietaryRestrictions: String, weatherOverview: String
    ) {

       val selectedMilkType = milkType.ifEmpty { "No Milk" }

        val prompt = """ Generate a coffee recipe based on the following preferences:
            - Mood: $mood
            - Sweetness Level: $sweetness
            - Milk Type: $selectedMilkType
            - Dietary Restrictions: $dietaryRestrictions
            - Weather: $weatherOverview

            Consider a variety of coffee-based drinks, including traditional coffees (e.g., espresso, americano), 
            flavored lattes, iced coffees, and international coffee variations (e.g., Vietnamese iced coffee, 
            Turkish coffee, affogato, or coffee with fruits like a strawberry matcha latte). 
            You can also suggest coffee infusions with spices or unique ingredients from 
            different cultures.
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

        // ingredients
        val ingredientsArray = coffeeRecipeJSON.getJSONArray("ingredients")
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


    private suspend fun generateRecipeImage(coffeeName: String): String {
        val apiKey = "Bearer ${BuildConfig.OPENAI_API_KEY}"
        val request =
            ImageRequest(prompt = "A high-quality, professional photograph of a beautifully crafted coffee drink in a cup, named '$coffeeName'. The coffee has rich textures, creamy froth, and is visually appealing. Shot in a cozy café setting.")

        return try {
            val response = openAIService.generateImage(apiKey, request)
            if (response.isSuccessful) {
                val imageUrl = response.body()?.data?.get(0)?.url ?: ""
                val firebaseImageUrl = uploadImageToFirebase(imageUrl)
                firebaseImageUrl
            } else {
                ""
            }

        } catch(e: Exception) {
            Log.i("AI Image", "Image generation error: ${e.message}")
            ""
        }
    }

    private suspend fun uploadImageToFirebase(imageUrl: String): String {

        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("coffee_images/${UUID.randomUUID()}.jpg")

        return try {
            val inputStream = withContext(Dispatchers.IO) {
                URL(imageUrl).openStream()
            }

            val uploadTask = imageRef.putStream(inputStream)

            uploadTask.await()
            val downloadUrl = imageRef.downloadUrl.await()

            downloadUrl.toString() // Return the download URL
        } catch (e: Exception) {
            Log.e("Firebase Image Upload", "Error uploading image: ${e.message}")
            ""
        }
    }


    fun fetchFavoriteRecipes() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userID).collection("favorites")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Favorites", "Error getting documents.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val recipesList = mutableListOf<CoffeeRecipe>()
                    for (doc in snapshots.documents) {
                        val recipeMap = doc.data ?: continue

                        val recipe = CoffeeRecipe(
                            name = recipeMap["name"] as String? ?: "",
                            ingredients = (recipeMap["ingredients"] as? List<*>)
                                ?.filterIsInstance<Map<String, Any>>()
                                ?.map {
                                    Ingredient(
                                        name = it["name"] as? String ?: "",
                                        amount = it["amount"] as? String ?: ""
                                    )
                                } ?: emptyList(),
                            instructions = (recipeMap["instructions"] as? List<*>)
                                ?.filterIsInstance<String>() ?: emptyList(),
                            imageUrl = recipeMap["imageUrl"] as? String ?: "",
                            isFavorite = recipeMap["isFavorite"] as? Boolean ?: false,
                            mood = recipeMap["mood"] as? String ?: "",
                            weather = recipeMap["weather"] as? String ?: "",
                            sweetness = recipeMap["sweetness"] as? String ?: "",
                            milkType = recipeMap["milkType"] as? String ?: "",
                            dietaryRestrictions = recipeMap["dietaryRestrictions"] as? String ?: ""
                        )
                        recipesList.add(recipe)
                    }
                    _favoriteRecipes.value = recipesList
                }
            }
    }

    fun saveRecipeToFavorites(recipe: CoffeeRecipe) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val recipeMap = mapOf(
            "name" to recipe.name,
            "ingredients" to recipe.ingredients.map {
                mapOf("name" to it.name, "amount" to it.amount)
            },
            "instructions" to recipe.instructions,
            "imageUrl" to recipe.imageUrl,
            "isFavorite" to recipe.isFavorite,
            "mood" to recipe.mood,
            "weather" to recipe.weather,
            "sweetness" to recipe.sweetness,
            "milkType" to recipe.milkType,
            "dietaryRestrictions" to recipe.dietaryRestrictions
        )

        db.collection("users").document(userID).collection("favorites")
            .add(recipeMap)
            .addOnSuccessListener {
                fetchFavoriteRecipes()
                Log.d("Favorites", "Recipe saved successfully!")
            }
            .addOnFailureListener { e ->
                Log.w("Favorites", "Error saving recipe", e)
            }
    }

    fun removeRecipeFromFavorites(recipe: CoffeeRecipe) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userID).collection("favorites")
            .whereEqualTo("name", recipe.name)
            .get().addOnSuccessListener { docs ->
                for (doc in docs) {
                    db.collection("users").document(userID)
                        .collection("favorites").document(doc.id).delete()
                }
                fetchFavoriteRecipes()
            }
            .addOnFailureListener { e ->
                Log.e("Favorites", "Error removing favorite", e)
            }
    }
}