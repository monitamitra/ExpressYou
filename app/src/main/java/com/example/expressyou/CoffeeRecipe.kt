package com.example.expressyou

data class CoffeeRecipe (
    val name: String,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val imageUrl: String,
    var isFavorite: Boolean = false,
    val mood: String,
    val weather: String,
    val sweetness: String,
    val milkType: String,
    val dietaryRestrictions: List<String>
)