package com.example.expressyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.expressyou.ui.theme.ExpressYouTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        setContent {
            ExpressYouTheme {
                MainScreen(recipeViewModel)
            }
        }
    }
}
