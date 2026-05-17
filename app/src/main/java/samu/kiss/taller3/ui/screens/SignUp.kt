package samu.kiss.taller3.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.compose.AppTheme
import samu.kiss.taller3.models.RegisterState
import samu.kiss.taller3.models.RegisterViewModel
import samu.kiss.taller3.ui.components.AuthEmailText
import samu.kiss.taller3.ui.components.AuthIdText
import samu.kiss.taller3.ui.components.AuthPasswordText
import samu.kiss.taller3.ui.components.AuthPlainText
import samu.kiss.taller3.ui.components.AuthTemplate
import samu.kiss.taller3.ui.components.MyButton
import samu.kiss.taller3.ui.components.MyButtonStyle

@Composable
fun SignUpScreen(
    controller: NavHostController, registerViewModel: RegisterViewModel = viewModel()
) {
    val state = registerViewModel.registerState.collectAsState().value

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> registerViewModel.updatePhoto(uri) }

    AuthTemplate {
        SignUpForm(
            state = state, isFormValid = registerViewModel.isFormValid(),

            onNameChange = { name ->
                registerViewModel.updateName(name)
            },

            onLastNameChange = { lastName ->
                registerViewModel.updateLastname(lastName)
            },

            onIdChange = { id ->
                registerViewModel.updateIdNumber(id)
            },

            onEmailChange = { email ->
                registerViewModel.updateEmail(email)
            },

            onPasswordChange = { password ->
                registerViewModel.updatePassword(password)
            },

            onPickPhoto = {
                imagePicker.launch("image/*")
            },

            onRegister = {
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
    state: RegisterState,
    isFormValid: Boolean,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onIdChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPickPhoto: () -> Unit,
    onRegister: () -> Unit,
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
                .border(1.2.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onPickPhoto() }, contentAlignment = Alignment.Center
            ) {
                if (state.photoUri != null) {
                    AsyncImage(
                        model = state.photoUri,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.AddAPhoto,
                        contentDescription = "Seleccionar foto",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = "Toca para seleccionar foto",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Campo: Nombre
            AuthPlainText(
                value = state.name,
                onValueChange = onNameChange,
                label = "Nombres",
                placeholder = "John"
            )
            // Campo: Apellidos
            AuthPlainText(
                value = state.lastname,
                onValueChange = onLastNameChange,
                label = "Apellidos",
                placeholder = "Doe"
            )
            // Campo: Identificación
            AuthIdText(
                value = state.idNumber, onValueChange = onIdChange
            )
            // Campo: Correo
            AuthEmailText(
                value = state.email,
                onValueChange = onEmailChange,
                label = "Correo",
                placeholder = "correo@gmail.com"
            )
            // Campo: Contraseña
            AuthPasswordText(
                value = state.password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                placeholder = "********"
            )

            Spacer(modifier = Modifier.height(4.dp))
            //Errores de FB
            if (state.firebaseError.isNotEmpty()) {
                Text(
                    text = state.firebaseError, style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                MyButton(
                    text = "Registrarse",
                    onClick = onRegister,
                    style = MyButtonStyle.Primary,
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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