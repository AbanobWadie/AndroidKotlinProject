package com.weatherforecast.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Favorite")
class Favorite: Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    lateinit var title: String
    var lat: Double = 0.0
    var lon: Double = 0.0
}