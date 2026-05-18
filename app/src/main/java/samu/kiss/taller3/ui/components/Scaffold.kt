package samu.kiss.taller3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import samu.kiss.taller3.models.UserLocationViewModel
import samu.kiss.taller3.navigation.AppScreens

@Composable
fun MyScaffold(
    navController: NavHostController,
    locationViewModel: UserLocationViewModel,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(contentWindowInsets = WindowInsets(0), topBar = {
        MyTopAppBar(
            navController = navController, locationViewModel = locationViewModel
        )
    }, content = { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = paddingValues.calculateTopPadding() / 100)
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
    val coroutineScope = rememberCoroutineScope()
    val isAvailable by locationViewModel.isAvailable.collectAsState()
    val glassBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.10f)
        )
    )

    Row(
        modifier = Modifier
            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.large)
            .background(brush = glassBrush, shape = MaterialTheme.shapes.large)
            .border(
                width = Dp.Hairline,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                shape = MaterialTheme.shapes.large
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Title
        Text(
            text = "Taller 3",
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        // Actions (menu)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { showMenu = !showMenu }) {
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
                    .background(MaterialTheme.colorScheme.surface)
                    .background(brush = glassBrush)
                    .border(
                        width = Dp.Hairline,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                DropdownMenuItem(text = {
                    Text("Inicio", color = MaterialTheme.colorScheme.tertiary)
                }, leadingIcon = {
                    Icon(
                        Icons.Rounded.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }, onClick = {
                    showMenu = false
                    navController.navigate(AppScreens.Home.name) { launchSingleTop = true }
                })

                DropdownMenuItem(text = {
                    Text("Personas", color = MaterialTheme.colorScheme.tertiary)
                }, leadingIcon = {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }, onClick = {
                    showMenu = false
                    navController.navigate(AppScreens.Users.name) { launchSingleTop = true }
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
                    coroutineScope.launch {
                        delay(500)
                        showMenu = false
                    }
                })

                DropdownMenuItem(text = {
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.tertiary)
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
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                })
            }
        }
    }
}

/*
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
