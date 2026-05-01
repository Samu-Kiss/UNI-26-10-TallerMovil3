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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import samu.kiss.myapplication.ui.components.AuthEmailText
import samu.kiss.myapplication.ui.components.AuthPasswordText
import samu.kiss.myapplication.ui.components.AuthTemplate
import samu.kiss.myapplication.ui.components.MyButton
import samu.kiss.myapplication.ui.components.MyButtonStyle

@Composable
fun LogInScreen(
    controller: NavHostController
) {
    AuthTemplate {
        LogInForm { email, password ->
            //TODO: Implementar lógica de inicio de sesión con email y password
        }
    }
}

@Composable
fun LogInForm(
    modifier: Modifier = Modifier,
    //https://kotlinlang.org/docs/lambdas.html#higher-order-functions
    email: String = "",
    password: String = "",
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onClick: (email: String, password: String) -> Unit = { _, _ -> },
) {
    val emailRegex = Regex("""^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$""")
    val passwordRegex =
        Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")
    val isLoginEnabled = emailRegex.matches(email) && passwordRegex.matches(password) && !isLoading

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
                .padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AuthEmailText(
                value = email,
                onValueChange = onEmailChange,
                label = "Correo",
                placeholder = "correo@gmail.com"
            )

            Spacer(modifier = Modifier.height(4.dp))

            AuthPasswordText(
                value = password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                placeholder = "********"
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage, style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error
                    ), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
            }

            // Loading o botones
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                MyButton(
                    text = "Iniciar Sesión",
                    onClick = { onClick(email, password) },
                    style = MyButtonStyle.Primary,
                    enabled = isLoginEnabled,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LogInScreenPreview() {
    AppTheme {
        LogInScreen(rememberNavController())
    }
}