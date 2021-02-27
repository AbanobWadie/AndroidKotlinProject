package com.weatherforecast.app.view

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

    var weatherRecyclerViewAdapter = WeatherRecyclerViewAdapter(arrayListOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherRecyclerView = findViewById(R.id.recyclerView)
        loading = findViewById(R.id.progressBar)

        val viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        observeViewModel(viewModel)
        initRecyclerViewList()
        viewModel.getData(33.441792, -94.037689, "current,minutely,hourly,alerts", "67bc71589f11ab9e108b887f0bab9bfc")
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
        weatherRecyclerViewAdapter.updateList(data.daily)
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

        builder.setPositiveButton("Yes") { dialog, which ->
            Toast.makeText(applicationContext,
                    "Yes", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(applicationContext,
                    "No", Toast.LENGTH_SHORT).show()
        }

        builder.setNeutralButton("Maybe") { dialog, which ->
            Toast.makeText(applicationContext,
                    "Maybe", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
}

fun String.toDate(dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}