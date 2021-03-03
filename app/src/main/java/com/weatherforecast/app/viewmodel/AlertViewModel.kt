package com.weatherforecast.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.AlertDao
import com.weatherforecast.app.model.AppDatabase
import kotlinx.coroutines.*

class AlertViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)!!
    private var alertDao: AlertDao = db.alertDao()

    private val alertInfo = MutableLiveData<List<Alert>>()
    private val loading = MutableLiveData<Boolean>()
    private val error = MutableLiveData<String>()

    fun insert(alert: Alert){
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
            loading.postValue(false)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            alertDao.insert(alert)
            withContext(Dispatchers.Main){
                loading.postValue(false)
            }
        }
    }

    fun getAll(){
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
            loading.postValue(false)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val response = alertDao.getAll()
            withContext(Dispatchers.Main){
                loading.postValue(false)
                if(response.isSuccessful){
                    alertInfo.postValue(response.body())
                }
            }
        }
    }

    fun getAlertInfo(): LiveData<List<Alert>> {
        return alertInfo
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun getError(): LiveData<String> {
        return error
    }
}