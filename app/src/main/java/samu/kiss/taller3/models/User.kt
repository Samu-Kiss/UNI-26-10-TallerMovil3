package samu.kiss.taller3.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MyUser(
    val uid: String = "",
    val name: String = "",
    val lastName: String = "",
    val idNumber: String = "",
    val photoUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val available: Boolean? = false
)

class MyUsersViewModel : ViewModel() {

    private val dbReference = FirebaseDatabase.getInstance().getReference("users")

    private val _users = MutableStateFlow<List<MyUser>>(emptyList())
    val users: StateFlow<List<MyUser>> = _users.asStateFlow()

    // Listener que se usará para escuchar cambios en Firebase Realtime Database
    private lateinit var listener: ValueEventListener

    init {
        //firebase.google.com/docs/database/android/offline-capabilities?hl=es-419#section-prioritizing-the-local-cache
        dbReference.keepSynced(true)

        // Se agrega un listener que escucha cualquier cambio en la referencia de la base de datos
        listener = dbReference.addValueEventListener(object : ValueEventListener {

            // Este metodo se ejecuta cada vez que los datos cambian
            // (al iniciar, al agregar, modificar o eliminar datos)
            override fun onDataChange(snapshot: DataSnapshot) {

                // Recorre todos los nodos hijos del snapshot y los convierte en objetos MyUser
                val updatedList = snapshot.children.mapNotNull { child ->

                    // Convierte el nodo actual en un objeto MyUser y le asigna como uid la clave del nodo en Firebase
                    child.getValue(MyUser::class.java)
                        ?.copy(uid = child.key ?: "")
                }

                // Log para verificar cuántos usuarios se cargaron
                Log.d("MyUsersViewModel", "Usuarios cargados: ${updatedList.size}")

                // Actualiza el StateFlow con la nueva lista para que la UI se refresque automáticamente
                _users.value = updatedList
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    "MyUsersViewModel",
                    "Error en Firebase: ${error.message}"
                )
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        dbReference.removeEventListener(listener)
    }
}