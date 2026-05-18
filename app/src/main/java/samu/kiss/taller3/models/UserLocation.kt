package samu.kiss.taller3.models

import android.app.Application
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import samu.kiss.taller3.auth
import samu.kiss.taller3.models.MyApp.Companion.fcmToken
import samu.kiss.taller3.utils.createLocationCallback
import samu.kiss.taller3.utils.createLocationRequest

data class UserLocationState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
)

// https://stackoverflow.com/questions/51451819/how-to-get-context-in-android-mvvm-viewmodel
class UserLocationViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(UserLocationState())
    val uiState: StateFlow<UserLocationState> = _uiState.asStateFlow()
    private val dbUsers = FirebaseDatabase.getInstance().getReference("users")
    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    // Listener en tiempo real sobre el nodo available del usuario actual
    private var availableListener: ValueEventListener? = null

    val context = getApplication<Application>()
    val locationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationCallback = createLocationCallback { result ->

        result.lastLocation?.let { location ->

            _uiState.update {
                it.copy(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    altitude = location.altitude
                )
            }
            Log.i("ULViewModel", "Ubicación recibida: ${location.latitude}, ${location.longitude}")

            // Guarda en Firebase si el usuario está disponible
            if (_isAvailable.value) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    // https://stackoverflow.com/questions/53317966/how-to-update-user-in-the-following-structure
                    dbUsers.child(uid).updateChildren(
                        mapOf(
                            "latitude" to location.latitude,
                            "longitude" to location.longitude
                        )
                    )
                } else {
                    Log.w("My Application", "No hay usuario autenticado, ubicación no guardada")
                }
            }
        }
    }

    var locationRequest: LocationRequest = createLocationRequest()
    var permissionGranted = false
    var vel: Task<Void>? = null

    fun observeAvailableFromFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Evitar registrar el listener dos veces si se llama más de una vez
        if (availableListener != null) return

        availableListener = dbUsers.child(uid).child("available")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Boolean::class.java) ?: false
                    _isAvailable.value = value
                    Log.d("UserLocationViewModel", "available sincronizado desde Firebase: $value")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("UserLocationViewModel", "Error escuchando available: ${error.message}")
                }
            })
    }

    fun setAvailable(available: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val update = mutableMapOf<String, Any>("available" to available)

        if (available) {
            val currentState = _uiState.value
            if (currentState.latitude != 0.0 && currentState.longitude != 0.0) {
                update["latitude"] = currentState.latitude
                update["longitude"] = currentState.longitude
            }
        }

        dbUsers.child(uid).updateChildren(update)
            .addOnFailureListener { e ->
                Log.w("My Application", "Error actualizando disponibilidad: ${e.localizedMessage}")
            }
    }


    fun signOut(onComplete: () -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null && availableListener != null) {
            dbUsers.child(uid).child("available").removeEventListener(availableListener!!)
            availableListener = null
        }
        if (uid != null) {
            dbUsers.child(uid).updateChildren(mapOf("available" to false))
                .addOnCompleteListener {
                    Firebase.messaging.token.addOnSuccessListener { token ->
                        fcmToken = token
                        FirebaseDatabase.getInstance().getReference("tokens/"+auth.currentUser!!.uid).setValue(null).addOnSuccessListener {
                            Log.i("FirebaseApp", "Token guardado correctamente")
                            FirebaseAuth.getInstance().signOut()
                        }.addOnFailureListener { e ->
                            Log.e("FirebaseApp", "Error guardando token: ${e.message}")
                            FirebaseAuth.getInstance().signOut()
                        }
                    }
                    //FirebaseAuth.getInstance().signOut()
                    _isAvailable.value = false
                    permissionGranted = false
                    onComplete()
                }
        } else {
            Firebase.messaging.token.addOnSuccessListener { token ->
                fcmToken = token
                FirebaseDatabase.getInstance().getReference("tokens/"+auth.currentUser!!.uid).setValue(null).addOnSuccessListener {
                    Log.i("FirebaseApp", "Token guardado correctamente")
                    FirebaseAuth.getInstance().signOut()
                }.addOnFailureListener { e ->
                    Log.e("FirebaseApp", "Error guardando token: ${e.message}")
                    FirebaseAuth.getInstance().signOut()
                }
            }
            _isAvailable.value = false
            permissionGranted = false
            onComplete()
        }
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
        // Limpiar listener de available si el ViewModel se destruye
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null && availableListener != null) {
            dbUsers.child(uid).child("available").removeEventListener(availableListener!!)
            availableListener = null
        }
    }
}