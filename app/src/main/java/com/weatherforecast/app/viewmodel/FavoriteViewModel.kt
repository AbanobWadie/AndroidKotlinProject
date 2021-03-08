package com.weatherforecast.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.weatherforecast.app.model.Favorite
import com.weatherforecast.app.model.datasource.internal.AppDatabase
import com.weatherforecast.app.model.datasource.internal.FavoriteDao
import kotlinx.coroutines.*

class FavoriteViewModel(application: Application): AndroidViewModel(application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)!!
    private val favoriteDao: FavoriteDao = db.favoriteDao()

    private val favoriteInfo = MutableLiveData<List<Favorite>>()
    private val error = MutableLiveData<String>()

    fun insertOrUpdate(favorite: Favorite){
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            favoriteDao.insert(favorite)
        }
    }

    fun getAll() {
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val data = favoriteDao.getAll()
            withContext(Dispatchers.Main) {
                favoriteInfo.postValue(data)
            }
        }
    }

    fun delete(favorite: Favorite){
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            favoriteDao.delete(favorite)
        }
    }

    fun getFavoriteInfo(): LiveData<List<Favorite>> {
        return favoriteInfo
    }

    fun getError(): LiveData<String> {
        return error
    }
}