package samu.kiss.taller3.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.navDeepLink
import samu.kiss.taller3.auth
import samu.kiss.taller3.models.UserLocationViewModel
import samu.kiss.taller3.ui.screens.*

enum class AppScreens {
    Splash, SignUp, LogIn, Home, Users, UserTracking
}

@Composable
fun Navigation(locationViewModel: UserLocationViewModel = viewModel(), notificationTargetUid: String? = null) {
    Log.i("NotifExp", "Pasando por nav")
    val navController = rememberNavController()
    var authReady by remember { mutableStateOf(false) }
    LaunchedEffect(notificationTargetUid, authReady) {
        if (authReady && notificationTargetUid != null) {
            navController.navigate("${AppScreens.UserTracking.name}/$notificationTargetUid") {
                popUpTo(AppScreens.Home.name) { inclusive = false }
            }
        }
    }

    NavHost(navController = navController, startDestination = AppScreens.Splash.name) {
        composable(route = AppScreens.Splash.name) {
            SplashScreen(navController, onAuthReady = { authReady = true })
        }
        composable(route = AppScreens.SignUp.name) {
            SignUpScreen(navController)
        }
        composable(route = AppScreens.LogIn.name) {
            LogInScreen(navController)
        }
        composable(route = AppScreens.Home.name) {
            HomeScreen(navController, locationViewModel)
        }
        composable(route = AppScreens.Users.name) {
            UsersScreen(navController,locationViewModel)
        }
        composable(
            route = "${AppScreens.UserTracking.name}/{targetUid}",
            arguments = listOf(
                navArgument("targetUid") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val targetUid =
                backStackEntry.arguments?.getString("targetUid").orEmpty()

            UserTrackingScreen(
                navController,
                targetUid,
                locationViewModel
            )
        }
    }
}