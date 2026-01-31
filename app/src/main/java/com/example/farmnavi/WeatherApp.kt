package com.example.farmnavi

import com.example.weatherapp.Clouds
import com.example.weatherapp.Coord
import com.example.weatherapp.Main
import com.example.weatherapp.Sys
import com.example.weatherapp.Weather
import com.example.weatherapp.Wind

data class WeatherApp(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)