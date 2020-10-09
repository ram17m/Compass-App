package com.example.compass_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.gps_fragment.*
import kotlinx.android.synthetic.main.gps_fragment.btnSaveMy
import kotlinx.android.synthetic.main.gps_fragment.tv_address
import kotlinx.android.synthetic.main.gps_fragment.tv_lat
import kotlinx.android.synthetic.main.gps_fragment.tv_lon
import kotlinx.android.synthetic.main.save_fragment.*
import java.util.*


class SaveFragment : Fragment(R.layout.save_fragment) {
    private var locationCallBack: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var lat = 0.0
    var lon = 0.0
    var add = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startLocationUpdates()

        val sharedPref = activity?.getSharedPreferences("location", Context.MODE_PRIVATE)

        btnSaveMy.setOnClickListener {
            sharedPref?.edit()?.apply {
                if (tv_lat.text.toString() != "") {
                    putString("latitude", lat.toString())
                }
                if (tv_lon.text.toString() != "") {
                    putString("longitude", lon.toString())
                }
                if (tv_address.text.toString() != "") {
                    putString("address", add)
                }
            }?.apply()
            val myToast = Toast.makeText(activity,"Location has been saved",Toast.LENGTH_SHORT)
            myToast.setGravity(Gravity.LEFT,215,-260)
            myToast.show()
        }

        btnSaveTarget.setOnClickListener {
            sharedPref?.edit()?.apply {
                if (targetLat.text.toString() != "") {
                    putString("latitude", targetLat.text.toString())
                }
                if (targetLat.text.toString() != "") {
                    putString("longitude", targetLon.text.toString())
                }
            }?.apply()
            val myToast = Toast.makeText(activity,"Location has been saved",Toast.LENGTH_SHORT)
            myToast.setGravity(Gravity.LEFT,215,408)
            myToast.show()
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                tv_lat.text = ("" + location.latitude)
                tv_lon.text = ("" + location.longitude)
                val geocoder = Geocoder(activity, Locale.getDefault())
                val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                tv_address.text = list[0].getAddressLine(0)

                lat = location.latitude
                lon = location.longitude
                add = list[0].getAddressLine(0)
            }
        }
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
            }
        }
    }

    private fun startLocationUpdates() {
        getLocation()

        //tv_updates.text = ("Location is now being tracked")
        tv_lat.text = ("")
        tv_lon.text = ("")
        tv_address.text = ("")
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallBack)

        //tv_updates.text = ("Location is not being tracked")
        tv_lat.text = ("Not tracking location")
        tv_lon.text = ("Not tracking location")
        tv_address.text = ("Not tracking location")
    }
}