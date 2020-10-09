package com.example.compass_app

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations.map
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.lifecycle.Transformations.map


class MapFragment : Fragment(R.layout.map_fragment) {
    private var mMap: GoogleMap? = null
    private lateinit var currentLocation: Location
    private val permissionCode = 101
    var lat = 0.0
    var lon = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getSharedPreferences("location", Context.MODE_PRIVATE)
        if (sharedPref != null) {
            var str = sharedPref.getString("latitude", "59.436962").toString()
            lat = str.toDouble()
            str = sharedPref.getString("longitude", "24.753574").toString()
            lon = str.toDouble()
        }

        /*
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
         */
    }

    fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val marker = LatLng(lat, lon)
        mMap!!.addMarker(MarkerOptions().position(marker).title("Marker is here"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(marker))
    }
}