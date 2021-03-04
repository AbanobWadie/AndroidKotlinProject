package com.weatherforecast.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alert")
data class Alert(@PrimaryKey(autoGenerate = true) var id: Int,var alertTime: String, var alertDay: List<Day>,
                 var alertEvent: String, var alertType: String, var enabled: Boolean,
                 val sender_name: String, val event: String, val start: Long, val end: Long, val description: String)
