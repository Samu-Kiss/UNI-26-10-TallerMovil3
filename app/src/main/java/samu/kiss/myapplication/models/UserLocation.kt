package samu.kiss.myapplication.models

import android.app.Application
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import samu.kiss.myapplication.utils.createLocationCallback
import samu.kiss.myapplication.utils.createLocationRequest

data class UserLocationState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
)

// https://stackoverflow.com/questions/51451819/how-to-get-context-in-android-mvvm-viewmodel
class UserLocationViewModel(application: Application): AndroidViewModel(application){
    private val _uiState = MutableStateFlow(UserLocationState())
    val uiState: StateFlow<UserLocationState> = _uiState.asStateFlow()
    private val dbUsers = FirebaseDatabase.getInstance().getReference("users")
    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    val context = getApplication<Application>()
    val locationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationCallback = createLocationCallback { result ->
        result.lastLocation?.let {
            _uiState.update {
                it.copy(
                    latitude = result.lastLocation!!.latitude,
                    longitude = result.lastLocation!!.longitude,
                    altitude = result.lastLocation!!.altitude
                )}

            Log.i("Informativo","${result}")

            if (_isAvailable.value) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    // https://stackoverflow.com/questions/53317966/how-to-update-user-in-the-following-structure
                    dbUsers.child(uid).updateChildren(
                        mapOf(
                            "latitude" to result.lastLocation!!.latitude,
                            "longitude" to result.lastLocation!!.longitude
                        )
                    ).addOnFailureListener { e ->
                        Log.w("My Application", "Error guardando ubicación: ${e.localizedMessage}")
                    }
                } else {
                    Log.w("My Application", "No hay usuario autenticado, ubicación no guardada")
                }
            }
        }
    }

    var locationRequest: LocationRequest = createLocationRequest()
    var permissionGranted = false
    var vel: Task<Void>? = null

    fun setAvailable(available: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        _isAvailable.value = available

        dbUsers.child(uid).updateChildren(mapOf("available" to available))
            .addOnFailureListener { e ->
                Log.w("My Application", "Error actualizando disponibilidad: ${e.localizedMessage}")
            }
    }

    fun signOut() {
        setAvailable(false)
        FirebaseAuth.getInstance().signOut()
        _isAvailable.value = false
        permissionGranted = false
    }

    fun updateVel() {
        Log.i("Informativo", "Logrado")
        if (!permissionGranted) {
            permissionGranted = true
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("Informativo", "Logrado2")
                vel = locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationClient.removeLocationUpdates(locationCallback)
    }
}
