package com.weatherforecast.app.model

data class WeatherInfo(val lat: Double, val log: Double, val timezone: String, val timezone_offset: Long, val daily: List<Daily>)
