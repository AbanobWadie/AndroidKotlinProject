package com.weatherforecast.app.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Daily
import org.joda.time.DateTime
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class WeatherRecyclerViewAdapter(var weatherData: ArrayList<Daily>): RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherViewHolder>() {

    lateinit var context: Context
    fun updateList(newList: List<Daily>) {
        weatherData.clear()
        weatherData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        context = parent.context
        return WeatherViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.weather_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        if (position == 7){
            holder.bind(weatherData[position], true, context)
        }else{
            holder.bind(weatherData[position], false, context)
        }
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateLbl = view.findViewById<TextView>(R.id.dateLbl)
        private val dayDescLbl = view.findViewById<TextView>(R.id.dayDescLbl)
        private val sunriseLbl = view.findViewById<TextView>(R.id.sunriseLbl)
        private val nightDescLbl = view.findViewById<TextView>(R.id.nightDescLbl)
        private val sunsetLbl = view.findViewById<TextView>(R.id.sunsetLbl)
        private val dayTempLbl = view.findViewById<TextView>(R.id.dayTempLbl)
        private val nightTempLbl = view.findViewById<TextView>(R.id.nightTempLbl)
        private val dayStatusImage = view.findViewById<ImageView>(R.id.dayStatusImage)
        private val nightStatusImage = view.findViewById<ImageView>(R.id.nightStatusImage)
        private val line = view.findViewById<View>(R.id.line)

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(daily: Daily, last: Boolean, context: Context) {
            val date = DateTime(daily.dt * 1000L)
            val dateFormat = SimpleDateFormat("E dd/MM")
            dateLbl.text = dateFormat.format(date.toDate())

            dayDescLbl.text = daily.weather[0].description
            nightDescLbl.text = daily.weather[0].description

            var time = DateTime(daily.sunrise * 1000L)
            val sdf = SimpleDateFormat("hh:mm a")
            sunriseLbl.text = context.getText(R.string.sunrise).toString() + " ${sdf.format(time.toDate())}"

            time = DateTime(daily.sunrise * 1000L)
            sunsetLbl.text = context.getText(R.string.sunset).toString() + " ${sdf.format(time.toDate())}"

            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            when (pref.getString("unit", "metric")!!) {
                "imperial" -> {
                    dayTempLbl.text = daily.temp.day.toInt().toString() + " ${context.getText(R.string.fahrenheit)}"
                    nightTempLbl.text = daily.temp.night.toInt().toString() + " ${context.getText(R.string.fahrenheit)}"
                }

                "metric" -> {
                    dayTempLbl.text = daily.temp.day.toInt().toString() + " ${context.getText(R.string.celsius)}"
                    nightTempLbl.text = daily.temp.night.toInt().toString() + " ${context.getText(R.string.celsius)}"
                }

                else -> {
                    dayTempLbl.text = daily.temp.day.toInt().toString() + " ${context.getText(R.string.kelvin)}"
                    nightTempLbl.text = daily.temp.night.toInt().toString() + " ${context.getText(R.string.kelvin)}"
                }
            }

            val options = RequestOptions()
                    .error(R.mipmap.ic_launcher_round)
            Glide.with(dayStatusImage.context)
                    .setDefaultRequestOptions(options)
                    .load("http://openweathermap.org/img/wn/" + daily.weather[0].icon + "@2x.png")
                    .into(dayStatusImage)

            val icon = StringBuilder()
            icon.append(daily.weather[0].icon)
            icon.deleteCharAt(icon.lastIndex)
            icon.append("n")
            Glide.with(nightStatusImage.context)
                .setDefaultRequestOptions(options)
                .load("http://openweathermap.org/img/wn/" + icon.toString() + "@2x.png")
                .into(nightStatusImage)

            if (last){
                line.visibility = View.GONE
            }else{
                line.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                val intent = Intent(context, MainDetailsActivity::class.java)
                intent.putExtra("title", "daily")
                intent.putExtra("daily", daily)
                context.startActivity(intent)
            }
        }
    }
}