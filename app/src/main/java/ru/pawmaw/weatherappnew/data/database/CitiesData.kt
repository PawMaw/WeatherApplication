package ru.pawmaw.weatherappnew.data.database

import ru.pawmaw.weatherappnew.data.models.CityModel
import org.json.JSONObject
import ru.pawmaw.weatherappnew.data.models.CityDetailDataModel
import java.text.SimpleDateFormat
import java.util.*

class CitiesData() {

    /**
     * Обработка запроса списка городов на главной странице
     */
    fun parseResponse(responseText: String): MutableList<CityModel> {
        val citiesList: MutableList<CityModel> = mutableListOf()
        val root = JSONObject(responseText)
        val ja = root.getJSONArray("city")
        for(index in 0 until ja.length()) {
            val jsonObject = ja.getJSONObject(index)
            val cityId = jsonObject.getString("city_id")
            val countryId = jsonObject.getString("country_id")
            val regionId = jsonObject.getString("region_id")
            val cityText = jsonObject.getString("name")
            val city = CityModel()
            city.city_id = cityId
            city.country_id = countryId
            city.region_id = regionId
            city.name = cityText
            citiesList.add(city)
        }

        return citiesList
    }

    /**
     * Обработка JSON элемента массива для вывода на детальной странице
     */
    fun getWeatherForSelectedDay (jsonObj: JSONObject): CityDetailDataModel {
        val main = jsonObj.getJSONObject("main")
        val wind = jsonObj.getJSONObject("wind")
        val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
        val updatedAt:Long = jsonObj.getLong("dt")
        val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
            Date(updatedAt*1000)
        )
        val temp = main.getString("temp")+"°C"
        val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
        val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
        val pressure = main.getString("pressure")
        val humidity = main.getString("humidity")
        val windSpeed = wind.getString("speed")
        val weatherDescription = weather.getString("description")
        val cityDetailData = CityDetailDataModel()

        cityDetailData.updatedAtText = updatedAtText
        cityDetailData.weatherDescription = weatherDescription
        cityDetailData.temp = temp
        cityDetailData.tempMax = tempMax
        cityDetailData.tempMin = tempMin
        cityDetailData.windSpeed = windSpeed
        cityDetailData.pressure = pressure
        cityDetailData.humidity = humidity

        return cityDetailData
    }
}