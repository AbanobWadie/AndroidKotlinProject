package com.weatherforecast.app.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.weatherforecast.app.R
import com.weatherforecast.app.model.WeatherInfo
import com.weatherforecast.app.view.alert.AlertActivity
import com.weatherforecast.app.view.alert.AlertBroadcastReceiver
import com.weatherforecast.app.view.alert.AlertService
import com.weatherforecast.app.view.favorite.FavoriteActivity
import com.weatherforecast.app.view.settings.SettingsActivity
import com.weatherforecast.app.viewmodel.WeatherViewModel
import org.joda.time.DateTime
import java.security.Provider
import java.text.SimpleDateFormat
import java.util.*


class MainActivity() : AppCompatActivity() {

    private lateinit var timeZoneLbl: TextView
    private lateinit var currentTimeLbl: TextView
    private lateinit var currentDescLbl: TextView
    private lateinit var currentTempLbl: TextView
    private lateinit var currentStatusImage: ImageView
    private lateinit var hourlyTitle: TextView
    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var dailyTitle: TextView
    private lateinit var dailyRecyclerView: RecyclerView
    private lateinit var loading: ProgressBar
    private lateinit var homeNavBar: BottomNavigationView

    private var weatherRecyclerViewAdapter = WeatherRecyclerViewAdapter(arrayListOf())
    private var hourlyRecyclerViewAdapter = HourlyRecyclerViewAdapter(arrayListOf())
    private var viewModel = WeatherViewModel()
    private var source = "home"
    private var locationFlag = true

    private var lat = 0.0F
    private var lon = 0.0F
    private var unit = String()
    private var language = String()
    private val myReceiver = AlertBroadcastReceiver()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeZoneLbl = findViewById(R.id.timeZoneLbl)
        currentTimeLbl = findViewById(R.id.currentTimeLbl)
        currentDescLbl = findViewById(R.id.currentDescLbl)
        currentTempLbl = findViewById(R.id.currentTempLbl)
        currentStatusImage = findViewById(R.id.currentStatusImage)
        hourlyTitle = findViewById(R.id.hourlyTitle)
        hourlyRecyclerView = findViewById(R.id.hourlyRecyclerView)
        dailyTitle = findViewById(R.id.dailyTitle)
        dailyRecyclerView = findViewById(R.id.dailyRecyclerView)
        loading = findViewById(R.id.homeProgressBar)
        homeNavBar = findViewById(R.id.homeNavBar)

        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        observeViewModel(viewModel)

        initRecyclerViewList()
        navBarMenuAction()
        setup()
    }

    override fun onResume() {
        super.onResume()
        if (intent.getStringExtra("source") != null) {
            source = intent.getStringExtra("source")!!
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        lat = pref.getFloat("lat", 0.0F)
        lon = pref.getFloat("lon", 0.0F)
        unit = pref.getString("unit", "metric")!!
        language = pref.getString("language", "en")!!
        val currentLocation = pref.getBoolean("currentLocation", true)

        if (source == "home") {
            if (currentLocation) {
                locationFlag = true
                getLocation()
            } else {
                setViewModel(lat.toDouble(), lon.toDouble())
            }
        }else{
            homeNavBar.visibility = View.GONE
            setViewModel(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lon", 0.0))
        }

        setLang()
    }

    override fun onBackPressed() {
        if (source != "home") {
            super.onBackPressed()
        }
    }

    private fun setViewModel(lat: Double, lon: Double){
        //viewModel.getData(33.441792, -94.037689, "metric", "current,minutely,hourly,alerts", "67bc71589f11ab9e108b887f0bab9bfc")
        viewModel.getData(lat, lon, unit, "minutely,alerts", language, "67bc71589f11ab9e108b887f0bab9bfc")
    }

    private fun initRecyclerViewList(){
        hourlyRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyRecyclerViewAdapter
        }

        dailyRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = weatherRecyclerViewAdapter
        }
    }

    private fun observeViewModel(viewModel: WeatherViewModel) {
        viewModel.getWeatherInfo().observe(this, Observer { setData(it) })
        viewModel.getLoading().observe(this, Observer { showLoading(it) })
        viewModel.getError().observe(this, Observer { showError(it) })
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setData(data: WeatherInfo) {
        Log.i("call", data.timezone)
        println(data)

        hourlyTitle.visibility = View.VISIBLE
        dailyTitle.visibility = View.VISIBLE

        hourlyRecyclerViewAdapter.updateList(data.hourly!!)
        weatherRecyclerViewAdapter.updateList(data.daily!!)

        timeZoneLbl.text = (data.timezone.split("/"))[1]
        val time = DateTime(data.current!!.dt * 1000L)
        val sdf = SimpleDateFormat("E hh:mm a")
        currentTimeLbl.text = sdf.format(time.toDate())
        currentDescLbl.text = data.current.weather[0].description
        when (unit) {
            "imperial" -> currentTempLbl.text = data.current.temp.toInt().toString() + " ${getText(R.string.fahrenheit)}"
            "metric" -> currentTempLbl.text = data.current.temp.toInt().toString() + " ${getText(R.string.celsius)}"
            else -> currentTempLbl.text = data.current.temp.toInt().toString() + " ${getText(R.string.kelvin)}"
        }

        val options = RequestOptions()
                .error(R.mipmap.ic_launcher_round)
        Glide.with(currentStatusImage.context)
                .setDefaultRequestOptions(options)
                .load("http://openweathermap.org/img/wn/" + data.current.weather[0].icon + "@2x.png")
                .into(currentStatusImage)

    }

    private fun showLoading(flag: Boolean) {
        Log.i("call", flag.toString())
        if(flag){
            loading.visibility = View.VISIBLE
        }else{
            loading.visibility = View.GONE
        }
    }

    private fun showError(msg: String) {
        Log.i("call", msg)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sorry")
        builder.setMessage(msg)

        builder.setPositiveButton("OK") { _, _ ->
            Toast.makeText(
                    applicationContext,
                    "OK", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }

    private fun getLocation() {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude

                Log.i("call", "Latitude: $latitude ; longitude: $longitude")
                val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val currentLocation = pref.getBoolean("currentLocation", true)
                if(currentLocation && locationFlag){
                    locationFlag = false
                    setViewModel(latitude, longitude)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        100
                )
                return
            }
            locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0f,
                    locationListener
            )
        } catch (ex: SecurityException) {
            Toast.makeText(applicationContext, ex.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setup() {
        val intent = Intent(this, AlertService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //startForegroundService(intent)
            val intentFilter = IntentFilter(Intent.ACTION_TIME_TICK)
            registerReceiver(myReceiver, intentFilter)
        }else{
            startService(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            unregisterReceiver(myReceiver)
        }
    }

    private fun navBarMenuAction() {
        homeNavBar.selectedItemId = R.id.navigation_home
        homeNavBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId){
                R.id.navigation_alert -> {
                    val intent = Intent(this, AlertActivity::class.java)
                    startActivity(intent)
                }

                R.id.navigation_favorite -> {
                    val intent = Intent(this, FavoriteActivity::class.java)
                    startActivity(intent)
                }

                R.id.navigation_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    private fun setLang() {
        if (intent.getBooleanExtra("openFlag", true)) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val resources: Resources = this.resources
            val config: Configuration = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFlag", false)
            startActivity(intent)
        }
    }

    private fun askPermission() {
        AlertDialog.Builder(this)
                .setTitle(getText(R.string.Permission))
                .setMessage(getText(R.string.PermissionMsg))
                .setPositiveButton(getText(R.string.yes)) { _, _ ->
                    val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + this.packageName)
                    )
                    startActivityForResult(intent, 0)
                }
                .setNegativeButton(getText(R.string.no), null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> showError(getText(R.string.rePermissionMsg).toString())
            }
        }
    }




}