package ru.pawmaw.weatherappnew.data.holders

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.pawmaw.weatherappnew.R
import ru.pawmaw.weatherappnew.data.models.CityModel
import ru.pawmaw.weatherappnew.ui.CityDetailActivity
import ru.pawmaw.weatherappnew.ui.CityDetailActivity.Companion.CUSTOM_CITY_TAG

class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cityName: TextView = itemView.findViewById(R.id.cityNameId)

    fun bind (cityModel: CityModel) {
        cityName.text = cityModel.name
        cityName.setOnClickListener {
            openDetailActivity(cityName.context, cityModel)
        }
    }

    private fun openDetailActivity (context: Context, cityModel: CityModel) {
        val intent = Intent(context, CityDetailActivity::class.java)
        intent.putExtra(CUSTOM_CITY_TAG, cityModel.name)
        context.startActivity(intent)
    }
}