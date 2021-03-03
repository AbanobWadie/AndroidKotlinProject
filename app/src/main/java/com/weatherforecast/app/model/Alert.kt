package com.weatherforecast.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alert")
data class Alert(@PrimaryKey(autoGenerate = true) var id: Int, val sender_name: String, val event: String, val start: Long, val end: Long, val description: String)
