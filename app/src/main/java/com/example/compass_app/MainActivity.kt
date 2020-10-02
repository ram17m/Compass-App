package com.example.compass_app

import android.content.Intent
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_FASTEST
import android.location.Location
import android.os.Bundle
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.toDegrees


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var img: ImageView
    private lateinit var mSensorManager: SensorManager
    private lateinit var mMagnetometer: Sensor
    private lateinit var mAccelerometer: Sensor

    private var currentDegree = 0.0f
    private var lastAccelerometer = FloatArray(3)
    private var lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        img = findViewById(R.id.imgViewCompass)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mMagnetometer = mSensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)
        mAccelerometer = mSensorManager.getDefaultSensor(TYPE_ACCELEROMETER)

        val btnGps = findViewById<Button>(R.id.btnGps)
        btnGps.setOnClickListener {
            val intent = Intent(this, GpsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        mSensorManager.registerListener(this, mMagnetometer, SENSOR_DELAY_FASTEST)
        mSensorManager.registerListener(this, mAccelerometer, SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()

        mSensorManager.unregisterListener(this, mMagnetometer)
        mSensorManager.unregisterListener(this, mAccelerometer)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == TYPE_ACCELEROMETER) {
            lowPass(event.values, lastAccelerometer)
            lastMagnetometerSet = true

        } else if (event.sensor.type == TYPE_MAGNETIC_FIELD) {
            lowPass(event.values, lastMagnetometer)
            lastAccelerometerSet = true
        }

        if (lastMagnetometerSet && lastAccelerometerSet) {
            val rm = FloatArray(9)
            if (SensorManager.getRotationMatrix(rm, null, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rm, orientation)

                /*
                var degree = Math.round(event.values[0]).toFloat()
                degree += declination?++?
                val bearing: Float = location.bearingTo(target)
                degree = (bearing - degree) * -1
                degree = normalizeDegree(degree)
                */

                val degree = (toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360

                val rotateAnimation = RotateAnimation(
                    currentDegree,
                    -degree,
                    RELATIVE_TO_SELF, 0.5f,
                    RELATIVE_TO_SELF, 0.5f
                )
                rotateAnimation.duration = 500
                rotateAnimation.fillAfter = true

                img.startAnimation(rotateAnimation)
                currentDegree = -degree

                lastMagnetometerSet = false
                lastAccelerometerSet = false
            }
        }
    }

    private fun normalizeDegree(value: Float): Float {
        if (value in 0.0F..180.0F) {
            return value
        } else {
            return 180.0F + (180.0F + value)
        }
    }

    /** A low Pass Filter for smoothing Sensor Data.
     *  0 ≤ alpha ≤ 1 ; use a smaller value for alpha for more smoothing.
     *  http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter
     **/

    private fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f

        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }
}