package com.example.compass_app

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.gps_fragment.*
import java.util.*

class GpsFragment : Fragment(R.layout.gps_fragment) {
    private var locationCallBack: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSaveMy.setOnClickListener {
            startLocationUpdates()

            val sharedPref = activity?.getSharedPreferences("location", Context.MODE_PRIVATE)
            sharedPref?.edit()?.apply {
                if (tv_lat.text != "")
                putBoolean("locUpdated", true)
            }?.apply()
        }

        tv_lat.text = ("Not tracking location")
        tv_lon.text = ("Not tracking location")
        tv_altitude.text = ("Not tracking location")
        tv_accuracy.text = ("Not tracking location")
        tv_speed.text = ("Not tracking location")
        tv_address.text = ("Not tracking location")
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {

                tv_lat.text = ("" + location.latitude)
                tv_lon.text = ("" + location.longitude)
                tv_altitude.text = ("" + location.altitude)
                tv_accuracy.text = ("" + location.accuracy)
                tv_speed.text = ("" + location.speed)

                val geocoder = Geocoder(activity, Locale.getDefault())
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

        tv_lat.text = ("")
        tv_lon.text = ("")
        tv_altitude.text = ("")
        tv_accuracy.text = ("")
        tv_speed.text = ("")
        tv_address.text = ("")
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallBack)

        //tv_updates.text = ("Location is not being tracked")
        tv_lat.text = ("Not tracking location")
        tv_lon.text = ("Not tracking location")
        tv_altitude.text = ("Not tracking location")
        tv_accuracy.text = ("Not tracking location")
        tv_speed.text = ("Not tracking location")
        tv_address.text = ("Not tracking location")
    }
}