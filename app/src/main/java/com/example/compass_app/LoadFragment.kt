package com.example.compass_app

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.compass_fragment.*
import kotlinx.android.synthetic.main.load_fragment.*

class LoadFragment : Fragment(R.layout.load_fragment), SensorEventListener {
    private var locationCallBack: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mSensorManager: SensorManager
    private lateinit var mMagnetometer: Sensor
    private lateinit var mAccelerometer: Sensor
    private lateinit var geoField: GeomagneticField

    private lateinit var destinationLoc: Location
    private var currentLoc = Location("location")

    private var north = false
    private var destLat = 0.0
    private var destLon = 0.0
    private var str = ""

    private var currentDegree = 0.0f
    private var lastAccelerometer = FloatArray(3)
    private var lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = activity?.getSharedPreferences("location", Context.MODE_PRIVATE)
        if (sharedPref != null) {
            str = sharedPref.getString("latitude", "59.436962").toString()
            destLat = str.toDouble()
            str = sharedPref.getString("longitude", "24.753574").toString()
            destLon = str.toDouble()
        }
        destinationLoc = Location("destination")
        destinationLoc.latitude = destLat
        destinationLoc.longitude = destLon
        getLocation()


        //mSensorManager = getSystemService(CompassFragment.SENSOR_SERVICE) as SensorManager
        mSensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()

        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()

        mSensorManager.unregisterListener(this, mMagnetometer)
        mSensorManager.unregisterListener(this, mAccelerometer)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            lowPass(event.values, lastAccelerometer)
            lastMagnetometerSet = true

        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            lowPass(event.values, lastMagnetometer)
            lastAccelerometerSet = true
        }

        if (lastMagnetometerSet && lastAccelerometerSet) {
            val rm = FloatArray(9)
            if (SensorManager.getRotationMatrix(rm, null, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(9)
                SensorManager.getOrientation(rm, orientation)

                geoField = GeomagneticField(
                    currentLoc.latitude.toFloat(),
                    currentLoc.longitude.toFloat(),
                    currentLoc.altitude.toFloat(),
                    System.currentTimeMillis()
                )

                var heading = ((Math.toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360)
                heading += geoField.declination

                val bearing = currentLoc.bearingTo(destinationLoc)
                Log.d("DBG", "bearing: " + bearing.toString())
                heading = (bearing - heading) * -1
                Log.d("DBG", "heading: " + heading.toString())
                //heading = normalizeDegree(heading)
                mRotate(heading)

            }
        }
    }

    /** A low Pass Filter for smoothing Sensor Data.
     *  0 ≤ alpha ≤ 1 ; use a smaller value for alpha for more smoothing.
     *  http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter
     **/

    private fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.02f

        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }

    private fun normalizeDegree(value: Float): Float {
        return if (value in 0.0F..180.0F) {
            value
        } else {
            180.0F + (180.0F + value)
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationClient =
            activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLoc = location
            }
        }
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
            }
        }
    }

    private fun mRotate(degree: Float) {
        val rotateAnimation = RotateAnimation(
            currentDegree,
            -degree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 300
        rotateAnimation.fillAfter = true

        imgViewNeedle.startAnimation(rotateAnimation)
        currentDegree = -degree

        lastMagnetometerSet = false
        lastAccelerometerSet = false
    }
}