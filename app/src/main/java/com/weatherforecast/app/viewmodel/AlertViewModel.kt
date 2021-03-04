package com.weatherforecast.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.datasource.internal.AlertDao
import com.weatherforecast.app.model.datasource.internal.AppDatabase
import kotlinx.coroutines.*

class AlertViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)!!
    private var alertDao: AlertDao = db.alertDao()

    private val alertInfo = MutableLiveData<List<Alert>>()
    private val alertSomeInfo = MutableLiveData<List<Alert>>()
    private val loading = MutableLiveData<Boolean>()
    private val error = MutableLiveData<String>()

    fun insertOrUpdate(alert: Alert){
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
            //loading.postValue(false)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            alertDao.insert(alert)
            withContext(Dispatchers.Main){
                //loading.postValue(false)
            }
        }
    }

    fun getAll(){
        loading.postValue(true)
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

    fun getSome(alertTime: String, enabled: Boolean){
        loading.postValue(true)
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
            loading.postValue(false)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val response = alertDao.getSome(alertTime, enabled)
            withContext(Dispatchers.Main){
                loading.postValue(false)
                if(response.isSuccessful){
                    alertSomeInfo.postValue(response.body())
                }
            }
        }
    }

    fun delete(alert: Alert){
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
            //loading.postValue(false)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            alertDao.delete(alert)
            withContext(Dispatchers.Main){
                //loading.postValue(false)
            }
        }
    }

    fun getAlertInfo(): LiveData<List<Alert>> {
        return alertInfo
    }

    fun getAlertSomeInfo(): LiveData<List<Alert>> {
        return alertSomeInfo
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun getError(): LiveData<String> {
        return error
    }
}