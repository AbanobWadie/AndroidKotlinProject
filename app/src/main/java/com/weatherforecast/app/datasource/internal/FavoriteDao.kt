package com.weatherforecast.app.datasource.internal

import androidx.room.*
import com.weatherforecast.app.model.Favorite

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Query("SELECT * FROM Favorite")
    fun getAll(): List<Favorite>

    @Delete
    suspend fun delete(favorite: Favorite)
}