package samu.kiss.myapplication.utils

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

fun createLocationRequest() : LocationRequest {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 200)
        .setWaitForAccurateLocation(true)
        .setMinUpdateIntervalMillis(100)
        .build()
    return locationRequest
}

fun createLocationCallback(onLocationChange : (LocationResult)-> Unit): LocationCallback {
    val callback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            onLocationChange(locationResult)
        }
    }
    return callback
}