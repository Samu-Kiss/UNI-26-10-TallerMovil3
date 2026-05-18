package samu.kiss.taller3.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import samu.kiss.taller3.auth
import samu.kiss.taller3.common.validEmailAddress
import samu.kiss.taller3.common.validPassword
import samu.kiss.taller3.models.MyApp.Companion.fcmToken

data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailErr: String = "",
    val passwordErr: String = "",
    val isLoading: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())

    val loginState = _loginState.asStateFlow()

    fun updateEmail(newValue: String) {
        _loginState.update { it.copy(email = newValue, emailErr = "") }
    }

    fun updatePassword(newValue: String) {
        _loginState.update { it.copy(password = newValue, passwordErr = "") }
    }

    private fun updateEmailError(newValue: String) {
        _loginState.update { it.copy(emailErr = newValue) }
    }

    private fun updatePasswordError(newValue: String) {
        _loginState.update { it.copy(passwordErr = newValue) }
    }

    private fun setLoading(value: Boolean) {
        _loginState.update { it.copy(isLoading = value) }
    }

    // Valida el formulario y actualiza los errores en el estado. Retorna true si es válido.
    fun validate(): Boolean {
        val email = _loginState.value.email
        val password = _loginState.value.password

        if (email.isEmpty()) {
            updateEmailError("El correo no puede estar vacío")
            return false
        }
        if (!validEmailAddress(email)) {
            updateEmailError("Correo electrónico inválido")
            return false
        }
        if (password.isEmpty()) {
            updatePasswordError("La contraseña no puede estar vacía")
            return false
        }
        if (!validPassword(password)) {
            updatePasswordError("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y símbolos")
            return false
        }
        return true
    }

    fun login(onSuccess: () -> Unit) {
        Log.i("FirebaseApp", "Ingresando")
        if (!validate()) return

        val email = _loginState.value.email
        val password = _loginState.value.password

        setLoading(true)
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            setLoading(false)
            Firebase.messaging.token.addOnSuccessListener { token ->
                fcmToken = token
                FirebaseDatabase.getInstance().getReference("tokens/"+auth.currentUser!!.uid).setValue(fcmToken).addOnSuccessListener {
                    Log.i("FirebaseApp", "Token guardado correctamente")
                    onSuccess()
                }.addOnFailureListener { e ->
                    Log.e("FirebaseApp", "Error guardando token: ${e.message}")
                    onSuccess()
                }
            }
        }.addOnFailureListener { e ->
            setLoading(false)
            updatePasswordError(e.localizedMessage ?: "Error al iniciar sesión")
        }
    }
}