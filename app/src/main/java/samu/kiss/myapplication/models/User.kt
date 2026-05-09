package samu.kiss.myapplication.models

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
    val available: Boolean = false
)

class MyUsersViewModel : ViewModel() {

    private val dbReference = FirebaseDatabase.getInstance().getReference("users")

    private val _users = MutableStateFlow<List<MyUser>>(emptyList())
    val users: StateFlow<List<MyUser>> = _users.asStateFlow()

    private val listener = dbReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val updatedList = mutableListOf<MyUser>()

            for (child in snapshot.children) {
                val user = child.getValue(MyUser::class.java)
                val uid = child.key ?: ""

                if (user != null) {
                    updatedList.add(user.copy(uid = uid))
                }
            }

            _users.value = updatedList
        }

        override fun onCancelled(error: DatabaseError) {
            // opcional: Log.e("MyUsersViewModel", error.message)
        }
    })

    override fun onCleared() {
        super.onCleared()
        dbReference.removeEventListener(listener)
    }
}