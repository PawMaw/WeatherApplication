package ru.pawmaw.weatherappnew.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import ru.pawmaw.weatherappnew.R
import ru.pawmaw.weatherappnew.data.models.CityDetailDataModel
import ru.pawmaw.weatherappnew.data.database.CitiesData
import java.net.URL


class CityDetailActivity : AppCompatActivity() {

    private val API_KEY: String = "e424c28c9f8068545f5132d0f335d586" // API ключ необходимый для запроса
    private var day = 0 // Итератор дней для выбора
    private var daysCount = 0
    private var weatherDataArray = JSONArray() // Массив с данными о погоде

    companion object {
        const val CUSTOM_CITY_TAG = "ru.pawmaw.weatherapplication.city_tag" // Константа для ведения журнала
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_detail)

        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        findViewById<TextView>(R.id.errorText).visibility = View.GONE

        val nextDateButton = findViewById<ImageView>(R.id.next_date_button)
        nextDateButton.setOnClickListener(nextDateListener)
        val prevDateButton = findViewById<ImageView>(R.id.prev_date_button)
        prevDateButton.setOnClickListener(prevDateListener)

        CoroutineScope(Dispatchers.Main).launch {
            intent?.extras?.getString(CUSTOM_CITY_TAG)?.let { setupActionBar(it) }
            intent?.extras?.getString(CUSTOM_CITY_TAG)?.let { getCityDetailData(it) }
        }
    }

    /**
     * Отрисовка Activity с полученными данными из запроса
     */
    private suspend fun getCityDetailData(cityName: String) {
        val result = getResults(cityName)
        try {
            val jsonObj = JSONObject(result)
            weatherDataArray = jsonObj.getJSONArray("list")
            refreshCurrentCityDetailData(
                CitiesData().getWeatherForSelectedDay(weatherDataArray[day] as JSONObject))
            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
        } catch (e: Exception) {
            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
        }
    }

    /**
     * Перерисовка Activity с новыми данными CityDetailData
     */
    private fun refreshCurrentCityDetailData(cityDetailDataModel: CityDetailDataModel) {
        findViewById<TextView>(R.id.updated_at).text =  cityDetailDataModel.updatedAtText
        findViewById<TextView>(R.id.status).text = cityDetailDataModel.weatherDescription.capitalize()
        findViewById<TextView>(R.id.temp).text = cityDetailDataModel.temp
        findViewById<TextView>(R.id.temp_min).text = cityDetailDataModel.tempMin
        findViewById<TextView>(R.id.temp_max).text = cityDetailDataModel.tempMax
        findViewById<TextView>(R.id.wind).text = cityDetailDataModel.windSpeed
        findViewById<TextView>(R.id.pressure).text = cityDetailDataModel.pressure
        findViewById<TextView>(R.id.humidity).text = cityDetailDataModel.humidity
    }

    /**
     * Coroutine запрос на получение данных по конкретному городу
     */
    private suspend fun getResults(cityName: String) = Dispatchers.Default {
        return@Default try{
            URL("https://api.openweathermap.org/data/2.5/forecast?q=$cityName&units=metric&appid=$API_KEY").readText(Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Listener для кнопки следующего временного отрезка погоды для города
     */
    private val nextDateListener = View.OnClickListener {
        when {
            day == 0 -> {
                findViewById<ImageView>(R.id.prev_date_button).visibility = View.VISIBLE
                day++
                refreshCurrentCityDetailData(
                    CitiesData().getWeatherForSelectedDay(weatherDataArray[day] as JSONObject))
            }
            day < weatherDataArray.length() - 1 -> {
                day++
                refreshCurrentCityDetailData(
                    CitiesData().getWeatherForSelectedDay(weatherDataArray[day] as JSONObject))
                if (day == weatherDataArray.length() - 1) {
                    findViewById<ImageView>(R.id.next_date_button).visibility = View.INVISIBLE
                }
            }
        }
    }

    /**
     * Listener для кнопки предыдущего временного отрезка погоды для города
     */
    private val prevDateListener = View.OnClickListener {
        when {
            day == 0 -> {
                findViewById<ImageView>(R.id.prev_date_button).visibility = View.INVISIBLE
            }
            day > 0 && day < weatherDataArray.length() -> {
                day--
                refreshCurrentCityDetailData(
                    CitiesData().getWeatherForSelectedDay(weatherDataArray[day] as JSONObject))
                if (day == 0) {
                    findViewById<ImageView>(R.id.prev_date_button).visibility = View.INVISIBLE
                } else {
                    findViewById<ImageView>(R.id.next_date_button).visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Пользовательский ActionBar с названием города на детальной странице
     */
    private fun setupActionBar(cityName : String) {
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = cityName
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt("CURRENT_DAY", day)
            putInt("ALL_DAYS", weatherDataArray.length())
            putInt("PREV_VISIBILITY", findViewById<ImageView>(R.id.prev_date_button).visibility)
            putInt("NEXT_VISIBILITY", findViewById<ImageView>(R.id.next_date_button).visibility)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        day = savedInstanceState.getInt("CURRENT_DAY")
        daysCount = savedInstanceState.getInt("ALL_DAYS")
        findViewById<ImageView>(R.id.prev_date_button).visibility = savedInstanceState.getInt("PREV_VISIBILITY")
        findViewById<ImageView>(R.id.next_date_button).visibility = savedInstanceState.getInt("NEXT_VISIBILITY")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}