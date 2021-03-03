package com.weatherforecast.app.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import retrofit2.Response

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: Alert)

    @Query("SELECT * FROM Alert")
    suspend fun getAll(): Response<List<Alert>>
}