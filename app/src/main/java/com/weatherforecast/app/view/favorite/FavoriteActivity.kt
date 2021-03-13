package com.weatherforecast.app.view.favorite

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Favorite
import com.weatherforecast.app.view.alert.AddAlertActivity
import com.weatherforecast.app.viewmodel.FavoriteViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.weatherforecast.app.view.alert.AlertActivity
import com.weatherforecast.app.view.main.MainActivity
import com.weatherforecast.app.view.settings.SettingsActivity

class FavoriteActivity : AppCompatActivity() {
    lateinit var favoriteRecyclerView: RecyclerView
    lateinit var favoriteNavbar: BottomNavigationView

    private var favoriteRecyclerViewAdapter = FavoriteRecyclerViewAdapter(arrayListOf())
    private lateinit var viewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        favoriteRecyclerView = findViewById(R.id.FavoriteRecyclerView)
        favoriteNavbar = findViewById(R.id.favoriteNavBar)

        val btn: FloatingActionButton = findViewById(R.id.addFavFloatingButton)
        btn.setOnClickListener {
            val intent = Intent(this, AddFavoriteActivity::class.java)
            startActivity(intent)
        }

        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
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
        favoriteRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = favoriteRecyclerViewAdapter
        }

//        val newFavorite = Favorite()
//        newFavorite.title = "chicago"
//        newFavorite.lat = 33.441792
//        newFavorite.lon = -94.037689
//
//        val newFavorite2 = Favorite()
//        newFavorite2.title = "istanbul"
//        newFavorite2.lat = 41.0082
//        newFavorite2.lon = 0.0
//        val data = listOf(newFavorite, newFavorite2)
//        favoriteRecyclerViewAdapter.updateList(data)
//
//        //viewModel.insertOrUpdate(newAlert)
    }

    private fun observeViewModel(viewModel: FavoriteViewModel) {
        viewModel.getFavoriteInfo().observe(this, Observer { updateUI(it) })
        viewModel.getError().observe(this, Observer { showError(it) })
    }

    private fun updateUI(data: List<Favorite>) {
        favoriteRecyclerViewAdapter.updateList(data, viewModel)
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

    private fun navBarMenuAction() {
        favoriteNavbar.selectedItemId = R.id.navigation_favorite
        favoriteNavbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId){
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                R.id.navigation_alert -> {
                    val intent = Intent(this, AlertActivity::class.java)
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
