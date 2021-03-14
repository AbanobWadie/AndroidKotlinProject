package com.weatherforecast.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.datasource.internal.AlertDao
import com.weatherforecast.app.datasource.internal.AppDatabase
import kotlinx.coroutines.*

class AlertViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)!!
    private val alertDao: AlertDao = db.alertDao()

    private val alertInfo = MutableLiveData<List<Alert>>()
    private val alertSomeInfo = MutableLiveData<List<Alert>>()
    private val error = MutableLiveData<String>()

    fun insertOrUpdate(alert: Alert){
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            alertDao.insert(alert)
        }
    }

    fun getAll() {
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val data = alertDao.getAll()
            withContext(Dispatchers.Main) {
                alertInfo.postValue(data)
            }
        }
    }

//    fun getSome(alertTime: String, enabled: Boolean){
//        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
//            error.postValue(th.localizedMessage)
//        }
//        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
//            val data = alertDao.getSome(alertTime, enabled)
//            withContext(Dispatchers.Main) {
//                alertSomeInfo.postValue(data)
//            }
//        }
//    }

    fun delete(alert: Alert){
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            alertDao.delete(alert)
        }
    }

    fun getAlertInfo(): LiveData<List<Alert>> {
        return alertInfo
    }

    fun getAlertSomeInfo(): LiveData<List<Alert>> {
        return alertSomeInfo
    }

    fun getError(): LiveData<String> {
        return error
    }
}