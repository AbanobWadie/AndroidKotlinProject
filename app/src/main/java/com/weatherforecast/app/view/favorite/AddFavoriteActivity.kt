package com.weatherforecast.app.view.favorite

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Favorite
import com.weatherforecast.app.viewmodel.FavoriteViewModel
import java.util.*

class AddFavoriteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var currentMarker : Marker? = null
    var fusedLocationProviderClient : FusedLocationProviderClient?= null
    var currentLocation: Location? = null
    var latitude: Double? = null
    var longtitue: Double? = null
    var title: String? = null
    private lateinit var viewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_favorite)

        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

    }

    // get Current location
    private fun fetchLocation() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1000)
            return
        }

        val task = fusedLocationProviderClient?.lastLocation
        task?.addOnSuccessListener { location ->
            if(location!=null)
            {
                this.currentLocation=location
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)

            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode)
        {
            1000->if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                fetchLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var latLong = LatLng(currentLocation?.latitude!!,currentLocation?.longitude!!)
        drawMarker(latLong)

        mMap.setOnMarkerDragListener(object :GoogleMap.OnMarkerDragListener{
            override fun onMarkerDrag(p0: Marker?) {

            }

            override fun onMarkerDragEnd(p0: Marker?) {
                if(currentMarker != null)
                    currentMarker?.remove()

                val newLatlong = LatLng(p0?.position!!.latitude, p0?.position!!.longitude)
                drawMarker(newLatlong)


                latitude = p0.position.latitude
                longtitue = p0.position.longitude
                Log.i("Lat","${latitude}")
                Log.i("Lat","${longtitue}")

                val newFavorite = Favorite()
                newFavorite.lat = latitude!!
                newFavorite.lon = longtitue!!
                newFavorite.title = title!!
                viewModel.insertOrUpdate(newFavorite)
                finish()
            }

            override fun onMarkerDragStart(p0: Marker?) {
            }
        })
    }
    private fun drawMarker(latLng: LatLng)
    {
        val markerOption= MarkerOptions().position(latLng).title("Iam here")
            .snippet(getAddress(latLng.latitude,latLng.longitude)).draggable(true)

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
        currentMarker= mMap.addMarker(markerOption)
        currentMarker?.showInfoWindow()

    }

    private fun getAddress(lat: Double, long: Double) :String? {
        val geCoder= Geocoder(this, Locale.getDefault())
        val address = geCoder.getFromLocation(lat,long,1)
        if (address!= null) {
            title = (address[0].locality)
        }
        return  address[0].getAddressLine(0).toString()
    }

}