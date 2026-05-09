package samu.kiss.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import samu.kiss.myapplication.models.RegisterViewModel
import samu.kiss.myapplication.ui.components.AuthEmailText
import samu.kiss.myapplication.ui.components.AuthIdText
import samu.kiss.myapplication.ui.components.AuthPasswordText
import samu.kiss.myapplication.ui.components.AuthPlainText
import samu.kiss.myapplication.ui.components.AuthTemplate
import samu.kiss.myapplication.ui.components.MyButton
import samu.kiss.myapplication.ui.components.MyButtonStyle

@Composable
fun SignUpScreen(
    controller: NavHostController, registerViewModel: RegisterViewModel = viewModel()
) {
    val state = registerViewModel.registerState.collectAsState().value

    AuthTemplate {
        SignUpForm(
            name = state.name,
            lastName = state.lastname,
            email = state.email,
            password = state.password,
            isLoading = state.isLoading,
            errorMessage = state.emailErr.ifEmpty { state.passwordErr }.ifEmpty { null },
            onNameChange = registerViewModel::updateName,
            onLastNameChange = registerViewModel::updateLastname,
            onEmailChange = registerViewModel::updateEmail,
            onPasswordChange = registerViewModel::updatePassword,
            onClick = {
                registerViewModel.register {
                    controller.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            })
    }
}

@Composable
fun SignUpForm(
    modifier: Modifier = Modifier,
    name: String = "",
    lastName: String = "",
    email: String = "",
    password: String = "",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onClick: () -> Unit = {},
) {
    var id by remember { mutableStateOf("") }
    val idRegex = Regex("""^\d{6,10}$""")



    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .blur(50.dp)
                .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo: Nombre
            AuthPlainText(
                value = name, onValueChange = onNameChange, label = "Nombres", placeholder = "John"
            )
            // Campo: Apellidos
            AuthPlainText(
                value = lastName,
                onValueChange = onLastNameChange,
                label = "Apellidos",
                placeholder = "Doe"
            )
            // Campo: Identificación
            AuthIdText(
                value = id,
                onValueChange = { id = it },
                label = "Identificación",
                placeholder = "123456",
            )
            // Campo: Correo
            AuthEmailText(
                value = email,
                onValueChange = onEmailChange,
                label = "Correo",
                placeholder = "correo@gmail.com"
            )
            // Campo: Contraseña
            AuthPasswordText(
                value = password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                placeholder = "********"
            )


            Spacer(modifier = Modifier.height(8.dp))

            // Botón: Registrarse


            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage, style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }

            MyButton(
                text = "Registrarse",
                onClick = onClick,
                style = MyButtonStyle.Primary,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    AppTheme {
        SignUpScreen(rememberNavController())
    }
}