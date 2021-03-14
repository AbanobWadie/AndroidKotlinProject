package com.weatherforecast.app.datasource.internal

import androidx.room.*
import com.weatherforecast.app.model.Alert

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: Alert)

    @Query("SELECT * FROM Alert")
    fun getAll(): List<Alert>

    @Query("SELECT * FROM Alert WHERE enabled=:enabled and alertTime=:alertTime")
    fun getSome(alertTime: String, enabled: Boolean): List<Alert>

    @Delete
    suspend fun delete(alert: Alert)
}