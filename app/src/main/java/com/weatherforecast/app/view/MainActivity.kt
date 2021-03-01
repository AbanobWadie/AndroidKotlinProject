package com.weatherforecast.app.view

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Daily
import com.weatherforecast.app.model.WeatherInfo
import com.weatherforecast.app.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var weatherRecyclerView: RecyclerView
    lateinit var loading: ProgressBar

    private var weatherRecyclerViewAdapter = WeatherRecyclerViewAdapter(arrayListOf())
    private var viewModel = WeatherViewModel()

    private var lat = 0.0F
    private var lon = 0.0F
    private var unit = String()
    private var language = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherRecyclerView = findViewById(R.id.recyclerView)
        loading = findViewById(R.id.progressBar)
        val btn: Button = findViewById(R.id.settingBtn)
        btn.setOnClickListener {
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        observeViewModel(viewModel)

        initRecyclerViewList()
    }

    override fun onResume() {
        super.onResume()
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        lat = pref.getFloat("lat", 0.0F)
        lon = pref.getFloat("lon", 0.0F)
        unit = pref.getString("unit", "metric")!!
        language = pref.getString("language", "en")!!
        val currentLocation = pref.getBoolean("currentLocation", true)
        if(currentLocation){
            getLocation()
        }else{
            setViewModel(lat.toDouble(), lon.toDouble())
        }
    }

    private fun setViewModel(lat: Double, lon: Double){
        //viewModel.getData(33.441792, -94.037689, "metric", "current,minutely,hourly,alerts", "67bc71589f11ab9e108b887f0bab9bfc")
        viewModel.getData(lat, lon, unit, "current,minutely,hourly,alerts", language, "67bc71589f11ab9e108b887f0bab9bfc")
    }

    private fun initRecyclerViewList(){
        weatherRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = weatherRecyclerViewAdapter
        }
    }

    private fun observeViewModel(viewModel: WeatherViewModel) {
        viewModel.getWeatherInfo().observe(this, Observer { setData(it) })
        viewModel.getLoading().observe(this, Observer { showLoading(it) })
        viewModel.getError().observe(this, Observer { showError(it) })
    }

    private fun setData(data: WeatherInfo) {
        weatherRecyclerViewAdapter.updateList(data.daily, data.timezone)
        Log.i("call", data.timezone)
        println(data)
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
            Toast.makeText(applicationContext,
                    "OK", Toast.LENGTH_SHORT).show()
        }

//        builder.setNegativeButton("No") { dialog, which ->
//            Toast.makeText(applicationContext,
//                    "No", Toast.LENGTH_SHORT).show()
//        }
//
//        builder.setNeutralButton("Maybe") { dialog, which ->
//            Toast.makeText(applicationContext,
//                    "Maybe", Toast.LENGTH_SHORT).show()
//        }
        builder.show()
    }

    private fun getLocation() {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        val locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                val latitude = p0.latitude
                val longitude = p0.longitude

                Log.i("call", "Latitude: $latitude ; longitude: $longitude")
                val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val currentLocation = pref.getBoolean("currentLocation", true)
                if(currentLocation){
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
                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
                return
            }
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex:SecurityException) {
            Toast.makeText(applicationContext, ex.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> showError("Please, give us permission to access your Location")
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}