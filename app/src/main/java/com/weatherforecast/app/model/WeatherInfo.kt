package com.weatherforecast.app.model

data class WeatherInfo(val lat: Double, val log: Double, val timezone: String, val timezone_offset: Long, val current: Current?, val hourly: List<Hourly>?, val daily: List<Daily>?, val alert: List<Alert>?)
