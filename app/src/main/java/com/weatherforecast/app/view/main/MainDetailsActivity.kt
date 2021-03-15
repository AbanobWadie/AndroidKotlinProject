package com.weatherforecast.app.view.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.datatransport.runtime.util.PriorityMapping.toInt
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Daily
import com.weatherforecast.app.model.Hourly
import org.joda.time.DateTime
import java.text.SimpleDateFormat

class MainDetailsActivity : AppCompatActivity() {

    lateinit var titleLbl: TextView
    lateinit var detailsDateLbl: TextView
    lateinit var detailsDescLbl: TextView
    lateinit var detailsTempLbl: TextView
    lateinit var detailsStatusImage: ImageView
    lateinit var firstValueLbl: TextView
    lateinit var secondValueLbl: TextView
    lateinit var thirdValueLbl: TextView
    lateinit var fourthValueLbl: TextView
    lateinit var fifthValueLbl: TextView
    lateinit var sixthValueLbl: TextView
    lateinit var seventhValueLbl: TextView
    lateinit var eighthValueLbl: TextView
    lateinit var ninthValueLbl: TextView

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_details)

        titleLbl = findViewById(R.id.titleLbl)
        detailsDateLbl = findViewById(R.id.detailsDateLbl)
        detailsDescLbl = findViewById(R.id.detailsDescLbl)
        detailsTempLbl = findViewById(R.id.detailsTempLbl)
        detailsStatusImage = findViewById(R.id.detailsStatusImage)
        firstValueLbl = findViewById(R.id.firstValueLbl)
        secondValueLbl = findViewById(R.id.secondValueLbl)
        thirdValueLbl = findViewById(R.id.thirdValueLbl)
        fourthValueLbl = findViewById(R.id.fourthValueLbl)
        fifthValueLbl = findViewById(R.id.fifthValueLbl)
        sixthValueLbl = findViewById(R.id.sixthValueLbl)
        seventhValueLbl = findViewById(R.id.seventhValueLbl)
        eighthValueLbl = findViewById(R.id.eighthValueLbl)
        ninthValueLbl = findViewById(R.id.ninthValueLbl)

        if (intent.getStringExtra("title") == "hourly"){
            setHourly()
        }else{
            setDaily()
        }
    }

    private fun setHourly() {
        val hourly = intent.getSerializableExtra("hourly") as Hourly

        titleLbl.text = getText(R.string.hourlyLbl)

        var time = DateTime(hourly.dt * 1000L)
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        detailsDateLbl.text = sdf.format(time.toDate())

        detailsDescLbl.text = hourly.weather[0].description

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        when (pref.getString("unit", "metric")!!) {
            "imperial" -> {
                detailsTempLbl.text = hourly.temp.toInt().toString() + " ${getText(R.string.fahrenheit)}"
                firstValueLbl.text = hourly.feels_like.toInt().toString() + " ${getText(R.string.fahrenheit)}"
                secondValueLbl.text = hourly.wind_speed.toInt().toString() + " ${getText(R.string.mileshour)}"
                fifthValueLbl.text = hourly.dew_point.toInt().toString() + " ${getText(R.string.fahrenheit)}"
            }

            "metric" -> {
                detailsTempLbl.text = hourly.temp.toInt().toString() + " ${getText(R.string.celsius)}"
                firstValueLbl.text = hourly.feels_like.toInt().toString() + " ${getText(R.string.celsius)}"
                secondValueLbl.text = hourly.wind_speed.toInt().toString() + " ${getText(R.string.metersec)}"
                fifthValueLbl.text = hourly.dew_point.toInt().toString() + " ${getText(R.string.celsius)}"
            }

            else -> {
                detailsTempLbl.text = hourly.temp.toInt().toString() + " ${getText(R.string.kelvin)}"
                firstValueLbl.text = hourly.feels_like.toInt().toString() + " ${getText(R.string.kelvin)}"
                secondValueLbl.text = hourly.wind_speed.toInt().toString() + " ${getText(R.string.metersec)}"
                fifthValueLbl.text = hourly.dew_point.toInt().toString() + " ${getText(R.string.kelvin)}"
            }
        }

        val options = RequestOptions()
            .error(R.mipmap.ic_launcher_round)
        Glide.with(detailsStatusImage.context)
            .setDefaultRequestOptions(options)
            .load("http://openweathermap.org/img/wn/" + hourly.weather[0].icon + "@2x.png")
            .into(detailsStatusImage)

        thirdValueLbl.text = hourly.pressure.toInt().toString() + " ${getText(R.string.hPa)}"
        fourthValueLbl.text = hourly.humidity.toInt().toString() + " %"
        sixthValueLbl.text = hourly.clouds.toInt().toString() + " %"
        seventhValueLbl.text = hourly.uvi.toInt().toString()
        if (hourly.rain != null){
            eighthValueLbl.text = hourly.rain.h!!.toInt().toString() + " ${getText(R.string.mm)}"
        }else{
            eighthValueLbl.text = "-"
        }
        if (hourly.snow != null){
            ninthValueLbl.text = hourly.snow.h!!.toInt().toString() + " ${getText(R.string.mm)}"
        }else{
            ninthValueLbl.text = "-"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDaily() {
        val daily = intent.getSerializableExtra("daily") as Daily

        titleLbl.text = getText(R.string.dailyLbl)

        var time = DateTime(daily.dt * 1000L)
        val sdf = SimpleDateFormat("E dd/MM/yyyy")
        detailsDateLbl.text = sdf.format(time.toDate())

        detailsDescLbl.text = daily.weather[0].description

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        when (pref.getString("unit", "metric")!!) {
            "imperial" -> {
                detailsTempLbl.text = daily.temp.day.toInt().toString() + "/" + daily.temp.night.toInt().toString() + " ${getText(R.string.fahrenheit)}"
                firstValueLbl.text = daily.feels_like.day.toInt().toString() + "/" + daily.feels_like.night.toInt().toString() + " ${getText(R.string.fahrenheit)}"
                secondValueLbl.text = daily.wind_speed.toInt().toString() + " ${getText(R.string.mileshour)}"
                fifthValueLbl.text = daily.dew_point.toInt().toString() + " ${getText(R.string.fahrenheit)}"
            }

            "metric" -> {
                detailsTempLbl.text = daily.temp.day.toInt().toString() + "/" + daily.temp.night.toInt().toString() + " ${getText(R.string.celsius)}"
                firstValueLbl.text = daily.feels_like.day.toInt().toString() + "/" + daily.feels_like.night.toInt().toString() + " ${getText(R.string.celsius)}"
                secondValueLbl.text = daily.wind_speed.toInt().toString() + " ${getText(R.string.metersec)}"
                fifthValueLbl.text = daily.dew_point.toInt().toString() + " ${getText(R.string.celsius)}"
            }

            else -> {
                detailsTempLbl.text = daily.temp.day.toInt().toString() + "/" + daily.temp.night.toInt().toString() + " ${getText(R.string.kelvin)}"
                firstValueLbl.text = daily.feels_like.day.toInt().toString() + "/" + daily.feels_like.night.toInt().toString() + " ${getText(R.string.kelvin)}"
                secondValueLbl.text = daily.wind_speed.toInt().toString() + " ${getText(R.string.metersec)}"
                fifthValueLbl.text = daily.dew_point.toInt().toString() + " ${getText(R.string.kelvin)}"
            }
        }

        val options = RequestOptions()
            .error(R.mipmap.ic_launcher_round)
        Glide.with(detailsStatusImage.context)
            .setDefaultRequestOptions(options)
            .load("http://openweathermap.org/img/wn/" + daily.weather[0].icon + "@2x.png")
            .into(detailsStatusImage)


        thirdValueLbl.text = daily.pressure.toInt().toString() + " ${getText(R.string.hPa)}"
        fourthValueLbl.text = daily.humidity.toInt().toString() + " %"
        sixthValueLbl.text = daily.clouds.toInt().toString() + " %"
        seventhValueLbl.text = daily.uvi.toInt().toString()
        if (daily.rain != null){
            eighthValueLbl.text = daily.rain.toInt().toString() + " ${getText(R.string.mm)}"
        }else{
            eighthValueLbl.text = "-"
        }
        if (daily.snow != null){
            ninthValueLbl.text = daily.snow.toInt().toString() + " ${getText(R.string.mm)}"
        }else{
            ninthValueLbl.text = "-"
        }
    }
}