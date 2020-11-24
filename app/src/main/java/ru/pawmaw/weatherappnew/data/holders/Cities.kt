package ru.pawmaw.weatherappnew.data.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.pawmaw.weatherappnew.R
import ru.pawmaw.weatherappnew.data.models.CityModel

class Cities (private val cities: List<CityModel>) : RecyclerView.Adapter<CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val rootView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.activity_city_item, parent, false)
        return CityViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }
}