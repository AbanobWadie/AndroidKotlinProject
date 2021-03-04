package com.weatherforecast.app.view.alert

import android.app.AlertDialog
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.Day
import com.weatherforecast.app.view.settings.SettingsActivity
import com.weatherforecast.app.viewmodel.AlertViewModel

class AlertActivity : AppCompatActivity() {
    lateinit var alertRecyclerView: RecyclerView
    lateinit var loading: ProgressBar

    private var alertRecyclerViewAdapter = AlertRecyclerViewAdapter(arrayListOf())
    //lateinit var viewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)

        alertRecyclerView = findViewById(R.id.alertRecyclerView)
        loading = findViewById(R.id.alerProgressBar)
        val btn: FloatingActionButton = findViewById(R.id.addFloatingButton)
        btn.setOnClickListener {
            val intent = Intent(this, AddAlertActivity::class.java)
            startActivity(intent)
        }

        //viewModel = ViewModelProvider(this).get(AlertViewModel::class.java)
        //observeViewModel(viewModel)

        initRecyclerViewList()
    }

    private fun initRecyclerViewList(){
        alertRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alertRecyclerViewAdapter
        }

        val data = listOf(
            Alert(1, "07:00 PM", listOf(Day.WEEKEND), "Temp", "Notification", false,
                "aa", "heat", 52165251, 5165156, "jsadjanfjNJKFLnJKFNJJNRJ\n sjkfbsj"),
            Alert(2, "10:00 AM", listOf(Day.MON, Day.WED), "wind", "Alarm", true,
                "bb", "storm", 8885585, 7855525, "ururururururu\n ,mmifmv")
        )
        alertRecyclerViewAdapter.updateList(data)
    }

    private fun observeViewModel(viewModel: AlertViewModel) {
        viewModel.getAlertInfo().observe(this, Observer { updateUI(it) })
        viewModel.getAlertSomeInfo().observe(this, Observer { navigate(it) })
        viewModel.getLoading().observe(this, Observer { showLoading(it) })
        viewModel.getError().observe(this, Observer { showError(it) })
    }

    private fun updateUI(data: List<Alert>) {
        alertRecyclerViewAdapter.updateList(data)
        println(data)
    }

    private fun navigate(data: List<Alert>) {

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