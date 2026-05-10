package samu.kiss.myapplication.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import samu.kiss.myapplication.models.UserLocationViewModel
import samu.kiss.myapplication.navigation.AppScreens

@Composable
fun MyScaffold(
    navController: NavHostController,
    locationViewModel: UserLocationViewModel,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(topBar = {
        MyTopAppBar(navController = navController, locationViewModel = locationViewModel)
    }, content = { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    navController: NavHostController, locationViewModel: UserLocationViewModel
) {

    var showMenu by remember { mutableStateOf(false) }
    val isAvailable by locationViewModel.isAvailable.collectAsState()
    val glassBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.10f)
        )
    )

    Box(
        modifier = Modifier.padding(16.dp)
    ) {
        TopAppBar(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(brush = glassBrush, shape = RoundedCornerShape(24.dp))
                .border(
                    width = Dp.Hairline,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                    shape = RoundedCornerShape(24.dp)
                ),
            title = {
                Text(
                    text = "Taller 3",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            actions = {
                IconButton(onClick = {
                    showMenu = !showMenu
                }) {
                    Icon(
                        Icons.Rounded.Menu,
                        contentDescription = "Open menu",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    shape = RoundedCornerShape(20.dp),
                    offset = DpOffset(x = 4.dp, y = (16).dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(brush = glassBrush)
                        .border(
                            width = Dp.Hairline,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    DropdownMenuItem(text = {
                        Text(
                            "Inicio", color = MaterialTheme.colorScheme.tertiary
                        )
                    }, leadingIcon = {
                        Icon(
                            Icons.Rounded.Home,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }, onClick = {
                        showMenu = false
                        navController.navigate(AppScreens.Home.name) {
                            launchSingleTop = true
                        }
                    })
                    DropdownMenuItem(text = {
                        Text(
                            "Personas", color = MaterialTheme.colorScheme.tertiary
                        )
                    }, leadingIcon = {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }, onClick = {
                        showMenu = false
                        navController.navigate(AppScreens.Users.name) {
                            launchSingleTop = true
                        }
                    })
                    DropdownMenuItem(text = {
                        Text(
                            text = if (isAvailable) "Disponible" else "No disponible",
                            color = if (isAvailable) Color(0xFF7DA67F) else MaterialTheme.colorScheme.tertiary
                        )
                    }, leadingIcon = {
                        Icon(
                            Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint = if (isAvailable) Color(0xFF7DA67F) else MaterialTheme.colorScheme.tertiary
                        )
                    }, onClick = {
                        locationViewModel.setAvailable(!isAvailable)
                        showMenu = false
                        Log.d("Disponibilidad", "Boton presionado de disponible")
                    })
                    DropdownMenuItem(text = {
                        Text(
                            "Cerrar sesión", color = MaterialTheme.colorScheme.tertiary
                        )
                    }, leadingIcon = {
                        Icon(
                            Icons.Rounded.ExitToApp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }, onClick = {
                        showMenu = false
                        locationViewModel.signOut {
                            navController.navigate(AppScreens.Splash.name) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    })
                }
            })
    }
}/*
@Preview
@Composable
fun MyScaffoldPreview() {
    MyScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello Scaffold!")
        }
    }
}*/
