package samu.kiss.myapplication.models

import androidx.lifecycle.ViewModel
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
    val emailErr: String = "",
    val passwordErr: String = "",
    val isLoading: Boolean = false
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

    fun updateEmail(newValue: String) {
        _registerState.update { it.copy(email = newValue, emailErr = "") }
    }

    fun updatePassword(newValue: String) {
        _registerState.update { it.copy(password = newValue, passwordErr = "") }
    }

    private fun updateEmailError(newValue: String) {
        _registerState.update { it.copy(emailErr = newValue) }
    }

    private fun updatePasswordError(newValue: String) {
        _registerState.update { it.copy(passwordErr = newValue) }
    }

    private fun setLoading(value: Boolean) {
        _registerState.update { it.copy(isLoading = value) }
    }

    // Valida el formulario y actualiza los errores en el estado. Retorna true si es válido.
    fun validate(): Boolean {
        val state = _registerState.value

        if (state.name.isBlank()) {
            updateEmailError("El nombre no puede estar vacío")
            return false
        }
        if (state.lastname.isBlank()) {
            updateEmailError("El apellido no puede estar vacío")
            return false
        }
        if (state.email.isEmpty()) {
            updateEmailError("El correo no puede estar vacío")
            return false
        }
        if (!validEmailAddress(state.email)) {
            updateEmailError("Correo electrónico inválido")
            return false
        }
        if (state.password.isEmpty()) {
            updatePasswordError("La contraseña no puede estar vacía")
            return false
        }
        if (!validPassword(state.password)) {
            updatePasswordError("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y símbolos")
            return false
        }
        return true
    }

    fun register(onSuccess: () -> Unit) {
        if (!validate()) return

        val email = _registerState.value.email
        val password = _registerState.value.password

        setLoading(true)
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            setLoading(false)
            onSuccess()
        }.addOnFailureListener { e ->
            setLoading(false)
            updateEmailError(e.localizedMessage ?: "Error al registrar")
        }
    }
}