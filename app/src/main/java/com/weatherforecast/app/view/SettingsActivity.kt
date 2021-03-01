package com.weatherforecast.app.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.weatherforecast.app.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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
                    AutocompleteActivity.RESULT_ERROR -> {
                        // TODO: Handle the error.
                        data?.let {
                            val status = Autocomplete.getStatusFromIntent(data)
                            //Log.i(TAG, status.statusMessage)
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        // The user canceled the operation.
                    }
                }
                return
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}