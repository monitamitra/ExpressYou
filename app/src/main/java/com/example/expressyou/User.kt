package com.example.expressyou

data class User (
    val email: String,
    val uid: String,
    val favorites: List<Map<String, Any>>
)