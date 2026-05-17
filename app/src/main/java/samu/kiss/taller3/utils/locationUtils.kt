package samu.kiss.taller3.utils

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

fun distance(lat1 : Double, long1: Double, lat2:Double, long2:Double) : Double{
    val latDistance = Math.toRadians(lat1 - lat2)
    val lngDistance = Math.toRadians(long1 - long2)
    val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)+
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    val result = 6378 * c;
    return Math.round(result*100.0)/100.0;
}