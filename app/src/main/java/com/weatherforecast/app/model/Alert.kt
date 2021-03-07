package com.weatherforecast.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Alert")
data class Alert(@PrimaryKey(autoGenerate = true) var id: Int, var alertTime: String, var alertDay: String,
    var alertEvent: String, var alertType: String, var enabled: Boolean,
    val event: String, val start: Long, val end: Long, val description: String): Serializable
