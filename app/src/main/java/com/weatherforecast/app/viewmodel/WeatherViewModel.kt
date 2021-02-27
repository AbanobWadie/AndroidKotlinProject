package com.weatherforecast.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weatherforecast.app.model.WeatherInfo
import com.weatherforecast.app.model.WeatherService
import kotlinx.coroutines.*

class WeatherViewModel: ViewModel() {

    private val weatherInfo = MutableLiveData<WeatherInfo>()
    private val loading = MutableLiveData<Boolean>()
    private val error = MutableLiveData<String>()

    fun getData(lat: Double, lon: Double, exclude: String, appId: String) {
        loading.postValue(true)
        val coroutineExceptionHandler = CoroutineExceptionHandler{  _, th ->
            error.postValue(th.localizedMessage)
            loading.postValue(false)
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val response = WeatherService.getWeatherService().getWeatherInfo(lat, lon, exclude, appId)
            withContext(Dispatchers.Main){
                loading.postValue(false)
                if(response.isSuccessful){
                    weatherInfo.postValue(response.body())
                }
            }
        }
    }

    fun getWeatherInfo(): LiveData<WeatherInfo>{
        return weatherInfo
    }

    fun getLoading(): LiveData<Boolean>{
        return loading
    }

    fun getError(): LiveData<String>{
        return error
    }
}