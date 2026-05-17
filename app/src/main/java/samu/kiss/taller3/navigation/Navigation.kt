package samu.kiss.taller3.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import samu.kiss.taller3.models.UserLocationViewModel
import samu.kiss.taller3.ui.screens.*

enum class AppScreens {
    Splash, SignUp, LogIn, Home, Users, UserTracking
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
            UsersScreen(navController,locationViewModel)
        }
        composable(
            route = "${AppScreens.UserTracking.name}/{targetUid}",
            arguments = listOf(navArgument("targetUid") { type = NavType.StringType })
        ) { backStackEntry ->
            val targetUid = backStackEntry.arguments?.getString("targetUid").orEmpty()
            UserTrackingScreen(navController, targetUid, locationViewModel)
        }
    }
}