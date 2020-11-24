package ru.pawmaw.weatherappnew.data.database

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import ru.pawmaw.weatherappnew.data.models.CityModel

class Database() {
    /**
     * Инициализация Realm для сохранения списка городов в памяти
     */
    fun initRealm(context: Context) {
        Realm.init(context)
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

    /**
     * Загрузка списка городов из памяти устройства
     */
    fun loadFromDB(): RealmResults<CityModel>? {
        val realm = Realm.getDefaultInstance()
        return realm.where(CityModel::class.java).findAll()
    }

    /**
     * Сохранение списка городов в память устройства
     */
    fun saveIntoDB(cities: List<CityModel>) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealm(cities)
        realm.commitTransaction()
    }
}
