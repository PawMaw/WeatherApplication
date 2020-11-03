package ru.pawmaw.weatherapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.pawmaw.weatherapplication.DetailActivity
import ru.pawmaw.weatherapplication.DetailActivity.Companion.CAT_FACT_TEXT_TAG
import ru.pawmaw.weatherapplication.R
import ru.pawmaw.weatherapplication.models.CityModel

class CitiesAdapter (private val cities: List<CityModel>) : RecyclerView.Adapter<CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val rootView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.city_item, parent, false)
        return CityViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities.get(position))
    }
}

class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cityName: TextView = itemView.findViewById(R.id.cityNameId)

    fun bind (cityModel: CityModel) {
        cityName.text = cityModel.name
        cityName.setOnClickListener {
            openDetailActivity(cityName.context, cityModel)
        }
    }

    private fun openDetailActivity (context: Context, cityModel: CityModel) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(CAT_FACT_TEXT_TAG, cityModel.name)
        context.startActivity(intent)
    }
}