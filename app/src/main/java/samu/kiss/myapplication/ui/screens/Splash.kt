package samu.kiss.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import kotlinx.coroutines.delay
import samu.kiss.myapplication.navigation.AppScreens
import samu.kiss.myapplication.ui.components.AuthTemplate
import samu.kiss.myapplication.ui.components.MyButton
import samu.kiss.myapplication.ui.components.MyButtonStyle

@Composable
fun SplashScreen(
    controller: NavHostController
) {
    AuthTemplate {
        var ctaVisible by remember { mutableStateOf(false) }
        var headerVisible by remember { mutableStateOf(false) }
        //val isPreview = LocalInspectionMode.current

        LaunchedEffect(Unit) {
            delay(400)
            headerVisible = true
            delay(800)
            ctaVisible = true

        }

        // Header animado en el centro
        AnimatedVisibility(
            visible = headerVisible,
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                animationSpec = tween(500), initialOffsetY = { -it / 2 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 64.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "TALLER 3",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "\t Por: Maria Alejandra García, \t \n  Juan David Ortiz y Samuel Pico",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
            }
        }

        // Botones animados en la parte inferior
        AnimatedVisibility(
            visible = ctaVisible,
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                animationSpec = tween(500), initialOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MyButton(
                    text = "Iniciar Sesión",
                    onClick = { controller.navigate(AppScreens.LogIn.name) },
                    modifier = Modifier.fillMaxWidth()
                )
                MyButton(
                    text = "Registrarse",
                    style = MyButtonStyle.Secondary,
                    onClick = { controller.navigate(AppScreens.SignUp.name) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    AppTheme {
        SplashScreen(rememberNavController())
    }
}