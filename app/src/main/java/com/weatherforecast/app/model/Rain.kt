package com.weatherforecast.app.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Rain(@SerializedName("1h") val h: Double?): Serializable
