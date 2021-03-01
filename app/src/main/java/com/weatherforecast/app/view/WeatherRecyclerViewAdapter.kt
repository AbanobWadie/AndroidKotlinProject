package com.weatherforecast.app.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Daily
import kotlinx.coroutines.currentCoroutineContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class WeatherRecyclerViewAdapter(var weatherData: ArrayList<Daily>): RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherViewHolder>() {

    var timezone = String()
    lateinit var context: Context
    fun updateList(newList: List<Daily>, timezone: String) {
        this.timezone = timezone

        weatherData.clear()
        weatherData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        context = parent.context
        return WeatherViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.weather_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weatherData[position], timezone, context)
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tempLbl = view.findViewById<TextView>(R.id.tempLbl)
        private val humidityLbl = view.findViewById<TextView>(R.id.humidityLbl)
        private val windLbl = view.findViewById<TextView>(R.id.windLbl)
        private val pressureLbl = view.findViewById<TextView>(R.id.pressureLbl)
        private val rainLbl = view.findViewById<TextView>(R.id.rainLbl)
        private val cloudsLbl = view.findViewById<TextView>(R.id.cloudsLbl)
        private val sunriseLbl = view.findViewById<TextView>(R.id.sunriseLbl)
        private val sunsetLbl = view.findViewById<TextView>(R.id.sunsetLbl)
        private val zoneLbl = view.findViewById<TextView>(R.id.zoneLbl)
        private val descriptionLbl = view.findViewById<TextView>(R.id.descriptionLbl)
        private val statusImageView = view.findViewById<ImageView>(R.id.statusImageView)

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(daily: Daily, timezone: String, context: Context) {
            tempLbl.text = daily.temp.day.toString()
            humidityLbl.text = daily.humidity.toString() + " %"
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            when (pref.getString("unit", "metric")!!) {
                "imperial" -> {
                    windLbl.text = daily.wind_speed.toString() + " miles/hour"
                }
                else -> {
                    windLbl.text = daily.wind_speed.toString() + " meter/sec"
                }
            }
            pressureLbl.text = daily.pressure.toString() +" hPa"
            if(daily.rain != null){
                rainLbl.text = daily.rain!!.toString() + " mm"
            }else{
                rainLbl.text = "-"
            }
            cloudsLbl.text = daily.clouds.toString() + " %"
            zoneLbl.text = timezone
            descriptionLbl.text = daily.weather[0].description

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")

                var instant = Instant.ofEpochMilli(daily.sunrise - 3600 * 12000)
                var date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                sunriseLbl.text = formatter.format(date) + " AM"

                instant = Instant.ofEpochMilli(daily.sunset - 3600 * 12000)
                date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                sunsetLbl.text = formatter.format(date) + " PM"
            }else{
                val sdf = SimpleDateFormat("HH:mm")
                var date = java.util.Date(daily.sunrise - 3600 * 12000)
                sunriseLbl.text = sdf.format(date) + " AM"

                date = java.util.Date(daily.sunset - 3600 * 12000)
                sunsetLbl.text = sdf.format(date) + " PM"
            }

            val options = RequestOptions()
                    .error(R.mipmap.ic_launcher_round)
            Glide.with(statusImageView.context)
                    .setDefaultRequestOptions(options)
                    .load("http://openweathermap.org/img/wn/" + daily.weather[0].icon + "@2x.png")
                    .into(statusImageView)
        }
    }
}