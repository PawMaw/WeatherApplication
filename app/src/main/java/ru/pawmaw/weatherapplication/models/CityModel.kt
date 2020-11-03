package ru.pawmaw.weatherapplication.models

import io.realm.RealmObject;

open class CityModel() : RealmObject() {
    lateinit var city_id : String
    lateinit var country_id : String
    lateinit var region_id : String
    lateinit var name : String
}
