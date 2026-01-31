package com.example.farmnavi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.farmnavi.databinding.FragmentWeatherBinding // Yahin par badlav hai
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null // Yahan bhi badlav hai
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false) // Aur yahan bhi
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchWeatherData("Gorakhpur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(
            cityName,
            "1b6b791064081bc2d789b4f225c19824",
            "metric"
        )
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val tempMax = responseBody.main.temp_max
                    val tempMin = responseBody.main.temp_min

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $tempMax °C"
                    binding.minTemp.text  = "Min Temp: $tempMin °C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windspeed m/s"
                    binding.sunrise.text = time(sunrise)
                    binding.sunset.text = time(sunset)
                    binding.sea.text = "$sealevel hPa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = cityName

                    changeImageAccordingToWeather(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("WeatherApp", "API call failed: ${t.message}")
                binding.temp.text = "Error fetching data"
            }
        })
    }

    private fun changeImageAccordingToWeather(condition: String) {
        // You need to implement this function to change the Lottie animation
        // based on the weather condition.
        // e.g., binding.lottieAnimationView.setAnimation(R.raw.rain)
    }

    private fun date(): CharSequence {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): CharSequence {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
