package com.weatherforecast.app.view.main

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
import com.weatherforecast.app.model.Hourly
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class HourlyRecyclerViewAdapter(var weatherData: ArrayList<Hourly>): RecyclerView.Adapter<HourlyRecyclerViewAdapter.HourlyViewHolder>() {

    lateinit var context: Context
    fun updateList(newList: List<Hourly>) {
        weatherData.clear()
        weatherData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        context = parent.context
        return HourlyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.hourly_list_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.bind(weatherData[position], context)
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val hourlyTimeLbl = view.findViewById<TextView>(R.id.hourlyTimeLbl)
        private val hourlyTempLbl = view.findViewById<TextView>(R.id.hourlyTempLbl)
        private val hourlyStatusImage = view.findViewById<ImageView>(R.id.hourlyStatusImage)

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(hourly: Hourly, context: Context) {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            when (pref.getString("unit", "metric")!!) {
                "imperial" -> hourlyTempLbl.text =
                    hourly.temp.toInt().toString() + " ${context.getText(R.string.fahrenheit)}"
                "metric" -> hourlyTempLbl.text =
                    hourly.temp.toInt().toString() + " ${context.getText(R.string.celsius)}"
                else -> hourlyTempLbl.text = hourly.temp.toInt().toString() + " ${context.getText(R.string.kelvin)}"
            }

            val time = DateTime(hourly.dt * 1000L)
            val sdf = SimpleDateFormat("hh:mm a")
            hourlyTimeLbl.text = sdf.format(time.toDate())

            val options = RequestOptions()
                    .error(R.mipmap.ic_launcher_round)
            Glide.with(hourlyStatusImage.context)
                    .setDefaultRequestOptions(options)
                    .load("http://openweathermap.org/img/wn/" + hourly.weather[0].icon + "@2x.png")
                    .into(hourlyStatusImage)
        }
    }
}