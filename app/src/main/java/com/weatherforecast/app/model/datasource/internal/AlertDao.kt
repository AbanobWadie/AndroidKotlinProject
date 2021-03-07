package com.weatherforecast.app.model.datasource.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.Day
import retrofit2.Response

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