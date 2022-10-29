package com.hong.assignment.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat


class CommonUtil private constructor() {

    companion object {
        @Volatile
        private var instance: CommonUtil? = null

        @JvmStatic
        fun getInstance(): CommonUtil =
            instance ?: synchronized(this) {
                instance ?: CommonUtil().also {
                    instance = it
                }
            }
    }

    private var applicationContext: Context? = null

    fun init(ctx: Context) {
        applicationContext = ctx
    }

    private fun getContext(): Context = applicationContext!!

    fun getLastLocation(): Location? {
        return if (ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val lm =
                getContext().getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            val gpsLocation = lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
            val gpsAccuracy = gpsLocation?.accuracy ?: -1F
            val networkLocation =
                lm.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
            val networkAccuracy = networkLocation?.accuracy ?: -1F
            if (gpsAccuracy > networkAccuracy) {
                gpsLocation
            } else {
                networkLocation
            }
        } else {
            null
        }
    }

}