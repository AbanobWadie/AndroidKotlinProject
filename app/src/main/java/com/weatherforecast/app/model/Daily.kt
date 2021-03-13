package com.weatherforecast.app.model

import java.io.Serializable

data class Daily(val dt: Long, val sunrise: Long, val sunset: Long, val temp: Temp, val feels_like: FeelsLike,
                 val pressure: Double, val humidity: Double, val dew_point: Double, val uvi: Double, val clouds: Double,
                 val wind_speed: Double, val wind_deg: Double?, val rain: Double?, val snow: Double?, val pop: Double, val weather: List<Weather>): Serializable
