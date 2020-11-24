package ru.pawmaw.weatherappnew

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import ru.pawmaw.weatherappnew.data.database.Database
import ru.pawmaw.weatherappnew.data.holders.Cities
import ru.pawmaw.weatherappnew.data.models.CityModel
import ru.pawmaw.weatherappnew.data.database.CitiesData
import java.util.*

class MainActivity : AppCompatActivity() {

    private val citiesJsonUrl = "https://raw.githubusercontent.com/aZolo77/citiesBase/master/cities.json" // Json файл с списком городов
    private val cities = ArrayList<CityModel>()
    private val shownCityList = ArrayList<CityModel>()
    private val sortedShownCityList = ArrayList<CityModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Database().initRealm(this)
        getCitiesFromServer(this, recyclerViewId)
    }

    override fun onResume() {
        super.onResume()
        Database().loadFromDB()
    }

    /**
     * Запрос на получение списка городов
     */
    private fun getCitiesFromServer(context: Context, recyclerViewId : RecyclerView) {
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET,
            citiesJsonUrl,
            Response.Listener { response ->
                cities.addAll(CitiesData().parseResponse(response))
                shownCityList.addAll(cities)
                Database().saveIntoDB(cities)
                val adapter =
                    Cities(shownCityList)
                recyclerViewId.adapter = adapter
                val layoutManager = LinearLayoutManager(context)
                recyclerViewId.layoutManager = layoutManager
            },
            Response.ErrorListener {
                Toast.makeText( context, "Ошибка запроса", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(stringRequest)
    }

    /**
     * Реализация поиска среди списка городов
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val menuItem = menu!!.findItem(R.id.app_bar_search)
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


