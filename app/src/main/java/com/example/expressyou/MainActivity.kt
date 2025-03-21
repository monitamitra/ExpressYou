package com.example.expressyou

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.expressyou.ui.theme.ExpressYouTheme

class MainActivity : ComponentActivity() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var weatherViewModel: WeatherViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                weatherViewModel.fetchWeatherData()
            } else {
                Toast.makeText(this, "Location permission required for recipe generation", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MAIN ACTIVITY: ", "onCreate called")

        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
        weatherViewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))[WeatherViewModel::class.java]

        // Check if permission is granted; otherwise, request it
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            Log.i("MAIN ACTIVITY: ", "FETCHING WEATHER DATA....")
            weatherViewModel.fetchWeatherData()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {
            ExpressYouTheme {
                MainScreen(recipeViewModel, weatherViewModel)
            }
        }
    }
}
