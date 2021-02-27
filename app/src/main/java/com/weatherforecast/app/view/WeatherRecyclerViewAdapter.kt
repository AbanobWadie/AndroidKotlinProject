package com.weatherforecast.app.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Daily
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class WeatherRecyclerViewAdapter(var weatherData: ArrayList<Daily>): RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherViewHolder>() {

    fun updateList(newList: List<Daily>) {
        weatherData.clear()
        weatherData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.weather_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weatherData[position])
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tempLbl = view.findViewById<TextView>(R.id.tempLbl)
        private val humidityLbl = view.findViewById<TextView>(R.id.humidityLbl)
        private val windLbl = view.findViewById<TextView>(R.id.windLbl)
        private val pressureLbl = view.findViewById<TextView>(R.id.pressureLbl)
        private val cloudsLbl = view.findViewById<TextView>(R.id.cloudsLbl)
        private val sunriseLbl = view.findViewById<TextView>(R.id.sunriseLbl)
        private val sunsetLbl = view.findViewById<TextView>(R.id.sunsetLbl)
        private val mainLbl = view.findViewById<TextView>(R.id.mainLbl)
        private val descriptionLbl = view.findViewById<TextView>(R.id.descriptionLbl)
        private val statusImageView = view.findViewById<ImageView>(R.id.statusImageView)

        fun bind(daily: Daily) {
            tempLbl.text = daily.temp.day.toString()
            humidityLbl.text = daily.humidity.toString()
            windLbl.text = daily.wind_speed.toString()
            pressureLbl.text = daily.pressure.toString()
            cloudsLbl.text = daily.clouds.toString()
            mainLbl.text = daily.weather[0].main
            descriptionLbl.text = daily.weather[0].description

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")

                var instant = Instant.ofEpochMilli(daily.sunrise)
                var date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                sunriseLbl.text = formatter.format(date)

                instant = Instant.ofEpochMilli(daily.sunset)
                date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                sunsetLbl.text = formatter.format(date)
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