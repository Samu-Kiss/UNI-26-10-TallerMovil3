package samu.kiss.myapplication.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import samu.kiss.myapplication.auth
import samu.kiss.myapplication.common.validEmailAddress
import samu.kiss.myapplication.common.validPassword

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
        if (!validate()) return

        val email = _loginState.value.email
        val password = _loginState.value.password

        setLoading(true)
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            setLoading(false)
            onSuccess()
        }.addOnFailureListener { e ->
            setLoading(false)
            updatePasswordError(e.localizedMessage ?: "Error al iniciar sesión")
        }
    }
}