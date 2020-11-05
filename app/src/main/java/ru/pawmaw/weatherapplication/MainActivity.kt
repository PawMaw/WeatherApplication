package ru.pawmaw.weatherapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import ru.pawmaw.weatherapplication.adapter.CitiesAdapter
import ru.pawmaw.weatherapplication.models.CityModel
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val citiesJsonUrl = "https://raw.githubusercontent.com/aZolo77/citiesBase/master/cities.json" // Json файл с списком городов

    val cities = ArrayList<CityModel>()
    val shownCityList = ArrayList<CityModel>()
    val sortedShownCityList = ArrayList<CityModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRealm()
        val queue = Volley.newRequestQueue(this)
        getCitiesFromServer(queue)
    }

    /**
     * Запрос на получение списка городов
     */
    private fun getCitiesFromServer(queue: RequestQueue) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            citiesJsonUrl,
            Response.Listener { response ->
                cities.addAll(parseResponse(response))
                shownCityList.addAll(cities)
                saveIntoDB(cities)
                loadFromDB()
                val adapter = CitiesAdapter(shownCityList)
                recyclerViewId.adapter = adapter
                val layoutManager = LinearLayoutManager(this)
                recyclerViewId.layoutManager = layoutManager
            },
            Response.ErrorListener {
                Toast.makeText( this, "Ошибка запроса", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequest)
    }

    /**
     * Сохранение списка городов в память устройства
     */
    private fun saveIntoDB(cities: List<CityModel>) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealm(cities)
        realm.commitTransaction()
    }

    /**
     * Загрузка списка городов из памяти устройства
     */
    private fun loadFromDB(): RealmResults<CityModel>? {
        var realm = Realm.getDefaultInstance()

        return realm.where(CityModel::class.java).findAll()
    }

    /**
     * Инициализация Realm для сохранения списка городов в памяти
     */
    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

    /**
     * Обработка запроса
     */
    private fun parseResponse(responseText: String): MutableList<CityModel> {
        var citiesList: MutableList<CityModel> = mutableListOf()
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
     * Реализация поиска среди списка городов
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu!!.findItem(R.id.searchCity)
        if (menuItem != null) {
            val searchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()) {
                        val search = newText.toLowerCase(Locale.getDefault())
                        shownCityList.forEach() {
                            if (it.name.toLowerCase(Locale.getDefault()).contains(search)) {
                                sortedShownCityList.add(it)
                            }
                        }
                        if (sortedShownCityList.isEmpty()) {
                            shownCityList.clear()
                            shownCityList.addAll(cities)
                            recyclerViewId.adapter!!.notifyDataSetChanged()
                        } else {
                            shownCityList.clear()
                            shownCityList.addAll(sortedShownCityList)
                            sortedShownCityList.clear()
                            recyclerViewId.adapter!!.notifyDataSetChanged()
                        }
                    } else {
                        shownCityList.clear()
                        shownCityList.addAll(cities)
                        recyclerViewId.adapter!!.notifyDataSetChanged()
                    }

                    return true
                }
            })
        }

        return super.onCreateOptionsMenu(menu)
    }
}