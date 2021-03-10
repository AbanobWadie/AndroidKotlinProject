package com.weatherforecast.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Alert")
class Alert(): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    lateinit var alertTime: String
    lateinit var alertDay: String
    lateinit var alertDayAr: String
    var alertEvent: Int = 0
    var alertType: Int = 0

    var enabled: Boolean = false
    lateinit var event: String
    var start: Long = 0L
    var end: Long = 0L
    lateinit var description: String
}
