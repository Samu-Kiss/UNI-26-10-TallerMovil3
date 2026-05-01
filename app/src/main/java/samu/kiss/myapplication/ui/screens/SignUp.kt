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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import samu.kiss.myapplication.ui.components.AuthEmailText
import samu.kiss.myapplication.ui.components.AuthIdText
import samu.kiss.myapplication.ui.components.AuthPasswordText
import samu.kiss.myapplication.ui.components.AuthPlainText
import samu.kiss.myapplication.ui.components.AuthTemplate
import samu.kiss.myapplication.ui.components.MyButton
import samu.kiss.myapplication.ui.components.MyButtonStyle

@Composable
fun SignUpScreen(
    controller: NavHostController
) {
    AuthTemplate {
        SignUpForm { email, password ->
            // TODO: Implementar lógica de registro con email y password
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpForm(
    modifier: Modifier = Modifier,
    //https://kotlinlang.org/docs/lambdas.html#higher-order-functions
    onClick: (email: String, password: String) -> Unit = { _, _ -> },
) {
    var names by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }

    val emailRegex = Regex("""^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$""")
    val passwordRegex =
        Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")
    val idRegex = Regex("""^\d{6,10}$""")

    val isSignUpEnabled =
        names.isNotBlank() && lastName.isNotBlank() && emailRegex.matches(email) && passwordRegex.matches(
            password
        ) && idRegex.matches(id)

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
                .border(
                    width = 1.2.dp,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo: Nombre
            AuthPlainText(
                value = names,
                onValueChange = { names = it },
                label = "Nombres",
                placeholder = "Nombres"
            )

            // Campo: Apellidos
            AuthPlainText(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Apellidos",
                placeholder = "Apellidos"
            )

            // Campo: Correo
            AuthEmailText(
                value = email,
                onValueChange = { email = it },
                label = "Correo",
                placeholder = "correo@gmail.com"
            )

            // Campo: Contraseña
            AuthPasswordText(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "********"
            )

            // Campo: Identificación
            AuthIdText(
                value = id,
                onValueChange = { id = it },
                label = "Identificación",
                placeholder = "123456"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón: Registrarse
            MyButton(
                text = "Registrarse",
                onClick = { onClick(email, password) },
                style = MyButtonStyle.Primary,
                enabled = isSignUpEnabled,
                modifier = Modifier.fillMaxWidth(),
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