package com.weatherforecast.app.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryName

interface WeatherApi {
    @GET("onecall")
    suspend fun getWeatherInfo(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("units") units: String,
                               @Query("exclude") exclude: String, @Query("lang") lang: String,
                               @Query("appid") appId: String): Response<WeatherInfo>
    }