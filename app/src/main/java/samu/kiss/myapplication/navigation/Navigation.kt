package samu.kiss.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import samu.kiss.myapplication.models.UserLocationViewModel
import samu.kiss.myapplication.ui.screens.*

enum class AppScreens {
    Splash, SignUp, LogIn, Home, Users
}

@Composable
fun Navigation(locationViewModel: UserLocationViewModel = viewModel()) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.Splash.name) {
        composable(route = AppScreens.Splash.name) {
            SplashScreen(navController)
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
            UsersScreen(navController)
        }
    }
}