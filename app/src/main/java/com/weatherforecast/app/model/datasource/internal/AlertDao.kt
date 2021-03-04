package com.weatherforecast.app.model.datasource.internal

import androidx.room.*
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.Day
import retrofit2.Response

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: Alert)

    @Query("SELECT * FROM Alert")
    suspend fun getAll(): Response<List<Alert>>

    @Query("SELECT * FROM Alert WHERE enabled=:enabled and alertTime=:alertTime")
    suspend fun getSome(alertTime: String, enabled: Boolean): Response<List<Alert>>

    @Delete
    suspend fun delete(alert: Alert)
}