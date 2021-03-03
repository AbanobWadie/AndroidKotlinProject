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
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.WeatherInfo
import com.weatherforecast.app.viewmodel.AlertViewModel
import com.weatherforecast.app.viewmodel.WeatherViewModel

class AlertActivity : AppCompatActivity() {
    lateinit var weatherRecyclerView: RecyclerView
    lateinit var loading: ProgressBar

    private var alertRecyclerViewAdapter = AlertRecyclerViewAdapter(arrayListOf())
    private var viewModel = AlertViewModel(application)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)

        viewModel = ViewModelProvider(this).get(AlertViewModel::class.java)
        observeViewModel(viewModel)
    }

    private fun initRecyclerViewList(){
        weatherRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alertRecyclerViewAdapter
        }
    }

    private fun observeViewModel(viewModel: AlertViewModel) {
        viewModel.getAlertInfo().observe(this, Observer { setData(it) })
        viewModel.getLoading().observe(this, Observer { showLoading(it) })
        viewModel.getError().observe(this, Observer { showError(it) })
    }

    private fun setData(data: List<Alert>) {
        alertRecyclerViewAdapter.updateList(data)
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
            Toast.makeText(
                applicationContext,
                "OK", Toast.LENGTH_SHORT
            ).show()
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
}