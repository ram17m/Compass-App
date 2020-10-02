package com.example.compass_app

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_gps.*
import java.util.*

class GpsActivity : AppCompatActivity() {
    private var locationCallBack: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)

        //var currentLocation = Location()
        //var savedLocations = List<Location>

        sw_locationsupdates.setOnClickListener {
            if (sw_locationsupdates.isChecked) {
                startLocationUpdates()
            } else {
                stopLocationUpdates()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                //currentLocation = location

                tv_lat.text = ("" + location.latitude)
                tv_lon.text = ("" + location.longitude)
                tv_altitude.text = ("" + location.altitude)
                tv_accuracy.text = ("" + location.accuracy)
                tv_speed.text = ("" + location.speed)

                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                tv_address.text = list[0].getAddressLine(0)
            }
        }
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
            }
        }
    }

    private fun startLocationUpdates() {
        getLocation()

        tv_updates.text = ("Location is now being tracked")
        tv_lat.text = ("")
        tv_lon.text = ("")
        tv_altitude.text = ("")
        tv_accuracy.text = ("")
        tv_speed.text = ("")
        tv_address.text = ("")
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallBack)

        tv_updates.text = ("Location is not being tracked")
        tv_lat.text = ("Not tracking location")
        tv_lon.text = ("Not tracking location")
        tv_altitude.text = ("Not tracking location")
        tv_accuracy.text = ("Not tracking location")
        tv_speed.text = ("Not tracking location")
        tv_address.text = ("Not tracking location")
    }
}