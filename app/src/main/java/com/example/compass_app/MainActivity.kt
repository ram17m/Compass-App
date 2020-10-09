package com.example.compass_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations.map
import com.google.android.gms.location.*
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.gps_fragment.*
import java.util.*


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(CompassFragment())

        val sharedPref = this.getSharedPreferences("location", Context.MODE_PRIVATE)
        val enableNeedle = sharedPref.getBoolean("locUpdated", false)
        btnLoad.isEnabled = enableNeedle
        btnComp.isEnabled = false

        btnComp.setOnClickListener {
            replaceFragment(CompassFragment())
            btnComp.isEnabled = false
        }

        btnGps.setOnClickListener {
            replaceFragment(GpsFragment())
            btnGps.isEnabled = false
        }

        btnSave.setOnClickListener {
            replaceFragment(SaveFragment())
            btnSave.isEnabled = false
        }

        btnLoad.setOnClickListener {
            replaceFragment(LoadFragment())
            btnLoad.isEnabled = false
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()

        btnComp.isEnabled = true
        btnGps.isEnabled = true
        btnSave.isEnabled = true
        btnLoad.isEnabled= true
    }
}