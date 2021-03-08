package com.weatherforecast.app.view.alert

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
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
import java.util.*

class AlertActivity : AppCompatActivity() {
    lateinit var alertRecyclerView: RecyclerView

    private var alertRecyclerViewAdapter = AlertRecyclerViewAdapter(arrayListOf())
    //private lateinit var viewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)

        alertRecyclerView = findViewById(R.id.alertRecyclerView)
        val btn: FloatingActionButton = findViewById(R.id.addFloatingButton)
        btn.setOnClickListener {
            val intent = Intent(this, AddAlertActivity::class.java)
            startActivity(intent)
        }

        //viewModel = ViewModelProvider(this).get(AlertViewModel::class.java)
        //observeViewModel(viewModel)

        initRecyclerViewList()

        val calendar = Calendar.getInstance()
        val currentDay = "${calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH)}"
        println("++++++++++++++Day+++++++++++ $currentDay")
    }

    override fun onResume() {
        super.onResume()
        // viewModel.getAll()
    }

    private fun initRecyclerViewList(){
        alertRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alertRecyclerViewAdapter
        }

        val newAlert = Alert()
        newAlert.alertTime = "07:00"
        newAlert. alertDay = "WEEKEND"
        newAlert.alertEvent = "Temp"
        newAlert. alertType = "Notification"
        newAlert.  enabled = true
        newAlert. event = ""
        newAlert.start = 0L
        newAlert. end = 0L
        newAlert.description = ""
        val data = listOf(newAlert)
        alertRecyclerViewAdapter.updateList(data)

        //viewModel.insertOrUpdate(newAlert)
    }

    private fun observeViewModel(viewModel: AlertViewModel) {
        viewModel.getAlertInfo().observe(this, Observer { updateUI(it) })
        viewModel.getAlertSomeInfo().observe(this, Observer { navigate(it) })
        viewModel.getError().observe(this, Observer { showError(it) })
    }

    private fun updateUI(data: List<Alert>) {
        alertRecyclerViewAdapter.updateList(data)
        println(data)
    }

    private fun navigate(data: List<Alert>) {

        println(data)
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