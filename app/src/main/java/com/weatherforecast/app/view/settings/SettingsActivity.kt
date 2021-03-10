package com.weatherforecast.app.view.settings

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.weatherforecast.app.R
import com.weatherforecast.app.view.alert.AlertActivity
import com.weatherforecast.app.view.favorite.FavoriteActivity
import com.weatherforecast.app.view.main.MainActivity
import java.util.*

class SettingsActivity : AppCompatActivity() {

    lateinit var settingsNavbar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        settingsNavbar = findViewById(R.id.settingsNavBar)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }

        navBarMenuAction()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    private fun navBarMenuAction() {
        settingsNavbar.selectedItemId = R.id.navigation_settings
        settingsNavbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId){
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                R.id.navigation_alert -> {
                    val intent = Intent(this, AlertActivity::class.java)
                    startActivity(intent)
                }

                R.id.navigation_favorite -> {
                    val intent = Intent(this, FavoriteActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private lateinit var locationListPreference: ListPreference
        private lateinit var locationEditTextPreference: EditTextPreference
        private lateinit var unitListPreference: ListPreference
        private lateinit var languageListPreference: ListPreference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            Places.initialize(requireContext(), "AIzaSyD1a36wBw4n7Jhjef3dT00W9R7hHOn_Cy0")
            val fieldList = listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(requireContext())
            val pref = PreferenceManager.getDefaultSharedPreferences(context)

            locationListPreference = findPreference("location")!!
            unitListPreference = findPreference("unit")!!
            languageListPreference = findPreference("language")!!
            locationEditTextPreference = findPreference("location_address")!!
            locationEditTextPreference.isVisible = !pref.getBoolean("currentLocation", true)

            locationListPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
                Log.i("call", value.toString())

                if(value.toString() == "other"){
                    startActivityForResult(intent, 100)
                }else{
                    pref.edit().putBoolean("currentLocation", true).apply()
                    locationEditTextPreference.isVisible = false
                }
                true
            }

            unitListPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
                Log.i("call", value.toString())
                pref.edit().putString("unit", value.toString()).apply()
                true
            }

            languageListPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
                Log.i("call", value.toString())
                pref.edit().putString("language", value.toString()).apply()
                setLang(value.toString())
                true
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == 100) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.let {
                            val place = Autocomplete.getPlaceFromIntent(data)
                            locationEditTextPreference.text = place.address
                            locationEditTextPreference.isVisible = true

                            val pref = PreferenceManager.getDefaultSharedPreferences(context)
                            pref.edit().putFloat("lat", place.latLng!!.latitude.toFloat()).apply()
                            pref.edit().putFloat("lon", place.latLng!!.longitude.toFloat()).apply()
                            pref.edit().putBoolean("currentLocation", false).apply()
                        }
                    }
                }
                return
            }
            super.onActivityResult(requestCode, resultCode, data)
        }

        private fun setLang(code: String) {
            val locale = Locale(code)
            Locale.setDefault(locale)
            val resources: Resources = requireContext().resources
            val config: Configuration = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)

            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}