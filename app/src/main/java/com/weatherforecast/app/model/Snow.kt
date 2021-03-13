package com.weatherforecast.app.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Snow(@SerializedName("1h") val h: Double?): Serializable
