package samu.kiss.myapplication.models

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import samu.kiss.myapplication.auth
import samu.kiss.myapplication.common.validEmailAddress
import samu.kiss.myapplication.common.validPassword

data class RegisterState(
    val name: String = "",
    val lastname: String = "",
    val email: String = "",
    val password: String = "",
    val idNumber: String = "",
    val photoUri: Uri? = null,
    val firebaseError: String = "",
    val isLoading: Boolean = false,
)

class RegisterViewModel : ViewModel() {
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    fun updateName(newValue: String) {
        _registerState.update { it.copy(name = newValue) }
    }
    fun updateLastname(newValue: String) {
        _registerState.update { it.copy(lastname = newValue) }
    }
    fun updateIdNumber(newValue: String) {
        _registerState.update { it.copy(idNumber = newValue) }
    }
    fun updateEmail(newValue: String) {
        _registerState.update { it.copy(email = newValue, firebaseError = "") }
    }
    fun updatePassword(newValue: String) {
        _registerState.update { it.copy(password = newValue, firebaseError = "") }
    }
    fun updatePhoto(uri: Uri?) = _registerState.update { it.copy(photoUri = uri) }

    fun isFormValid(): Boolean {
        val s = _registerState.value
        return s.name.isNotBlank() && s.lastname.isNotBlank() && s.idNumber.isNotBlank() && validEmailAddress(
            s.email
        ) && validPassword(s.password)
    }

    fun register(onSuccess: () -> Unit) {
        if (!isFormValid()) return

        val state = _registerState.value
        _registerState.update { it.copy(isLoading = true, firebaseError = "") }

        auth.createUserWithEmailAndPassword(state.email, state.password).addOnSuccessListener { result ->
            val uid = result.user!!.uid
            if (state.photoUri != null) {
                uploadPhotoThenSave(uid, state, onSuccess)
            } else {
                saveToDatabase(uid, state, photoUrl = "", onSuccess)
            }
        }.addOnFailureListener { e ->
            _registerState.update {
                it.copy(
                    isLoading = false,
                    firebaseError = e.localizedMessage ?: "Error al crear la cuenta"
                )
            }
        }
    }

    private fun uploadPhotoThenSave(uid: String, s: RegisterState, onSuccess: () -> Unit) {
        val ref = FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")
        ref.putFile(s.photoUri!!).continueWithTask { task ->
            if (!task.isSuccessful) throw task.exception!!
            ref.downloadUrl
        }.addOnSuccessListener { downloadUri ->
            saveToDatabase(uid, s, photoUrl = downloadUri.toString(), onSuccess)
        }.addOnFailureListener { e ->
            android.util.Log.w(
                "My Application", "Storage falló, guardando sin foto: ${e.localizedMessage}"
            )
            saveToDatabase(uid, s, photoUrl = "", onSuccess)
        }
    }

    private fun saveToDatabase(
        uid: String, s: RegisterState, photoUrl: String, onSuccess: () -> Unit
    ) {
        val user = MyUser(
            uid = uid,
            name = s.name,
            lastName = s.lastname,
            idNumber = s.idNumber,
            photoUrl = photoUrl,
            latitude = 0.0,
            longitude = 0.0,
            available = false,
        )
        FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(user)
            .addOnSuccessListener {
                _registerState.update { it.copy(isLoading = false) }
                onSuccess()
            }.addOnFailureListener { e ->
                _registerState.update {
                    it.copy(
                        isLoading = false,
                        firebaseError = e.localizedMessage ?: "Error al guardar el perfil"
                    )
                }
            }
    }
}