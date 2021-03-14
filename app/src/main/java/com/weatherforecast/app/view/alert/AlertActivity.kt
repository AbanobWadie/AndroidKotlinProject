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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.Day
import com.weatherforecast.app.view.favorite.FavoriteActivity
import com.weatherforecast.app.view.main.MainActivity
import com.weatherforecast.app.view.settings.SettingsActivity
import com.weatherforecast.app.viewmodel.AlertViewModel
import java.util.*

class AlertActivity : AppCompatActivity() {
    lateinit var alertRecyclerView: RecyclerView
    lateinit var alertNavbar: BottomNavigationView

    private var alertRecyclerViewAdapter = AlertRecyclerViewAdapter(arrayListOf())
    private lateinit var viewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)

        alertRecyclerView = findViewById(R.id.alertRecyclerView)
        alertNavbar = findViewById(R.id.alertNavBar)

        val btn: FloatingActionButton = findViewById(R.id.addFloatingButton)
        btn.setOnClickListener {
            val intent = Intent(this, AddAlertActivity::class.java)
            intent.putExtra("status","add")
            startActivity(intent)
        }

        viewModel = ViewModelProvider(this).get(AlertViewModel::class.java)
        observeViewModel(viewModel)

        initRecyclerViewList()
        navBarMenuAction()
    }

    override fun onResume() {
        super.onResume()
         viewModel.getAll()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    private fun initRecyclerViewList(){
        alertRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alertRecyclerViewAdapter
        }
    }

    private fun observeViewModel(viewModel: AlertViewModel) {
        viewModel.getAlertInfo().observe(this, Observer { updateUI(it) })
        viewModel.getAlertSomeInfo().observe(this, Observer { navigate(it) })
        viewModel.getError().observe(this, Observer { showError(it) })
    }

    private fun updateUI(data: List<Alert>) {
        alertRecyclerViewAdapter.updateList(data, viewModel)
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
        builder.show()
    }

    private fun navBarMenuAction() {
        alertNavbar.selectedItemId = R.id.navigation_alert
        alertNavbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId){
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
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
}