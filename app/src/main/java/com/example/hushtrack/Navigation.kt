package com.example.hushtrack

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Landing") {
        composable("Landing") { LandingScreen(navController) }
        composable("signin") { SignInScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("finish") { FinishScreen(navController) }
        composable("forgot") { ForgotPwdScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("home") { MajorScreen(navController = navController) }
        composable("settings") { SettingsScreen(navController = navController) }
        composable("resources") { ResourcesScreen(navController = navController) }
        composable("notifications") { NotificationScreen(navController = navController) }
        composable("support") { SupportScreen(navController = navController) }
        composable("new-report") { ReportScreen(navController = navController) }
    }
}