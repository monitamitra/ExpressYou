package com.example.expressyou

import android.app.Application
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherApiService = RetrofitInstance.weatherService
    private val _weatherOverview = MutableLiveData<NetworkResponse<String>>()
    val weatherOverview: LiveData<NetworkResponse<String>> get() = _weatherOverview


    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)


    fun fetchWeatherData() {
        // get and check location permissions
        val context = getApplication<Application>().applicationContext
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    getWeatherOverview(location.latitude, location.longitude)
                   // Log.i("FETCH WEATHER DATA: ", "RETREIVED LOCATION NOT NULL")
                } else {
                    _weatherOverview.value = NetworkResponse.Error("Location not available")
                }
            }
            .addOnFailureListener { exception ->
                _weatherOverview.value = NetworkResponse.Error(exception.message.toString())
            }
    }

    private fun getWeatherOverview(latitude: Double, longitude: Double) {
        Log.i("GET WEATHER OVERVIEW METHOD: ", "lat=$latitude, lon=$longitude")

        viewModelScope.launch{
            _weatherOverview.value = NetworkResponse.Loading

            try{
                val weatherResponse = weatherApiService.getWeatherSummary(
                    longitude = longitude,
                    latitude = latitude,
                    apiKey = BuildConfig.WEATHER_API_KEY
                )
                Log.i("WEATHER RESPONSE", "Response code: ${weatherResponse.code()}")
                Log.i("WEATHER RESPONSE", "Response body: ${weatherResponse.body()}")

                if (weatherResponse.isSuccessful) {
                    val body = weatherResponse.body()
                    if (body != null) {
                        val overview = body.weather_overview
                        Log.i("WEATHER DATA: ", latitude.toString())
                        _weatherOverview.value = NetworkResponse.Success(overview)
                    } else {
                        Log.e("WEATHER ERROR", "Weather response body is null")
                        _weatherOverview.value = NetworkResponse.Error("Weather overview returned NONE :(")
                    }
                }

            } catch(e: Exception) {
                Log.e("WEATHER ERROR", "Exception: ${e.message}")
                _weatherOverview.value = NetworkResponse.Error("Failed to generate recipe :(")
            }
        }
    }
}