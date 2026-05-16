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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.google.firebase.database.FirebaseDatabase
import samu.kiss.myapplication.auth
import samu.kiss.myapplication.models.LoginViewModel
import samu.kiss.myapplication.models.MyApp
import samu.kiss.myapplication.ui.components.AuthEmailText
import samu.kiss.myapplication.ui.components.AuthPasswordText
import samu.kiss.myapplication.ui.components.AuthTemplate
import samu.kiss.myapplication.ui.components.MyButton
import samu.kiss.myapplication.ui.components.MyButtonStyle

@Composable
fun LogInScreen(
    controller: NavHostController, loginViewModel: LoginViewModel = viewModel()
) {
    val state = loginViewModel.loginState.collectAsState().value

    AuthTemplate {
        LogInForm(
            email = state.email,
            password = state.password,
            isLoading = state.isLoading,
            errorMessage = state.emailErr.ifEmpty { state.passwordErr }.ifEmpty { null },
            onEmailChange = { loginViewModel.updateEmail(it) },
            onPasswordChange = { loginViewModel.updatePassword(it) },
            onClick = {
                loginViewModel.login {
                    controller.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            })
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
    onClick: () -> Unit = {},
) {

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

            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage, style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error
                    ), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                MyButton(
                    text = "Iniciar Sesión",
                    onClick = onClick,
                    style = MyButtonStyle.Primary,
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