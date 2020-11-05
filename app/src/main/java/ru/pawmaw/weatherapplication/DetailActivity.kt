package ru.pawmaw.weatherapplication

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    var day = 0;
    var weatherArray = JSONArray()
    companion object {
        const val CAT_FACT_TEXT_TAG = "ru.pawmaw.weatherapplication.city_tag"
    }

    val API: String = "e424c28c9f8068545f5132d0f335d586"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.city_detail)
        intent?.extras?.getString(CAT_FACT_TEXT_TAG)?.let { setupActionBar(it) }
        intent?.extras?.getString(CAT_FACT_TEXT_TAG)?.let { weatherTask(it).execute()}
    }

    private fun setupActionBar(cityName : String) {
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = cityName
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    inner class weatherTask(cityName: String) : AsyncTask<String, Void, String>() {
        val CITY : String = cityName;
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE

            val nextDateButton = findViewById<ImageView>(R.id.next_date_button)
            nextDateButton.setOnClickListener(nextDateListener)
            val prevDateButton = findViewById<ImageView>(R.id.prev_date_button)
            prevDateButton.setOnClickListener(prevDateListener)
        }
        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/forecast?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                weatherArray = jsonObj.getJSONArray("list")
                println(weatherArray.length())
                getWeatherForSelectedDay(weatherArray[day] as JSONObject)
            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }

        private fun getWeatherForSelectedDay (jsonObj : JSONObject) {
            try {
                val main = jsonObj.getJSONObject("main")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }
        private val nextDateListener = View.OnClickListener {
            when {
                day == 0 -> {
                    findViewById<ImageView>(R.id.prev_date_button).visibility = View.VISIBLE
                    day++
                    getWeatherForSelectedDay(weatherArray[day] as JSONObject)
                }
                day < weatherArray.length() - 1 -> {
                    day++
                    getWeatherForSelectedDay(weatherArray[day] as JSONObject)
                    if (day == weatherArray.length() - 1) {
                        findViewById<ImageView>(R.id.next_date_button).visibility = View.INVISIBLE
                    }
                }
            }
        }
        private val prevDateListener = View.OnClickListener {
            when {
                day == 0 -> {
                    findViewById<ImageView>(R.id.prev_date_button).visibility = View.INVISIBLE
                }
                day > 0 && day < weatherArray.length() -> {
                    day--
                    getWeatherForSelectedDay(weatherArray[day] as JSONObject)
                    if (day == 0) {
                        findViewById<ImageView>(R.id.prev_date_button).visibility = View.INVISIBLE
                    } else {
                        findViewById<ImageView>(R.id.next_date_button).visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}