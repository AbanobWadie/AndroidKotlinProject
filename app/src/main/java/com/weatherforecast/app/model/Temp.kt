package com.weatherforecast.app.model

import java.io.Serializable

data class Temp(val day: Double, val min: Double, val max: Double, val night: Double, val eve: Double, val morn: Double): Serializable
