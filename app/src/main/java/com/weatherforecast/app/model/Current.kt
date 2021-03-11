package com.weatherforecast.app.model

data class Current(val dt: Long, val sunrise: Long, val sunset: Long, val temp: Double, val feels_like: Double,
                   val pressure: Double, val humidity: Double, val dew_point: Double, val uvi: Double, val clouds: Double,
                   val wind_speed: Double, val wind_deg: Double?, val rain: Rain?, val snow: Snow?, val pop: Double, val weather: List<Weather>)
