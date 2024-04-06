package com.example.weathercondition

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weathercondition.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import android.widget.SearchView
import java.util.concurrent.locks.Condition

// 308b2cf23bb499dc3a3dd30517beb91b
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeatherData("Malegaon")
        SearchCity()
    }


    private fun SearchCity() {
        val searchView = binding.searchView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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



    private fun fetchWeatherData(cityName : String){
       val retrofit = Retrofit.Builder()
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl("https://api.openweathermap.org/data/2.5/")
           .build().create(ApiInterface::class.java)

       val response = retrofit.getWeatherData(cityName,"308b2cf23bb499dc3a3dd30517beb91b","metric")
       response.enqueue(object : Callback<WeatherApp>{
           override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
               val responseBody = response.body()
               if(response.isSuccessful && responseBody != null ){
                   val temperature = responseBody.main.temp.toString()
                   val humidity =  responseBody.main.humidity
                   val windSpeed = responseBody.wind.speed
                   val sunRise = responseBody.sys.sunrise.toLong()
                   val sunSet = responseBody.sys.sunset.toLong()
                   val seaLevel = responseBody.main.pressure
                   val condition = responseBody.weather.firstOrNull()?.main?:"unkonwn"
                   val maxTemp = responseBody.main.temp_max
                   val minTemp = responseBody.main.temp_min
                   val weather = responseBody.weather.firstOrNull()?.main?:"unkonwn"

                   binding.temp.text="$temperature °C"
                   binding.weather1.text="$condition"
                   binding.weather.text="$weather"
                   binding.maxTemp.text="MAX Temp = $maxTemp °C"
                   binding.minTemp.text="MIN Temp = $minTemp °C"
                   binding.humidity.text="$humidity %"
                   binding.windSpeed.text="$windSpeed m/s"
                   binding.sunRise.text="${time(sunRise)}"
                   binding.sunSet.text="${time(sunSet)}"
                   binding.sea.text="$seaLevel hPa"
                   binding.day.text =dayName(System.currentTimeMillis())
                       binding.date.text =date()
                       binding.cityName.text="$cityName"



                   // Log.d("TAG", "onResponse: Temperature is $temperature")
                   changeImageAccordingToWeather(condition)
               }
           }

           override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
               TODO("Not yet implemented")
           }

       })

    }

    private fun changeImageAccordingToWeather(conditions: String) {
        when(conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}