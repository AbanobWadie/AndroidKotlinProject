package com.weatherforecast.app.model.datasource.internal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.Favorite

@Database(entities = [Alert::class, Favorite::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun alertDao(): AlertDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

         fun getDatabase(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "App_database"
                        )
                            .build()
                    }
                }
            }
            return instance
        }
    }
}