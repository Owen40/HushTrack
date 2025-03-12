package com.example.hushtrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authManager = FireBaseAuthManager()

    NavHost(navController = navController, startDestination = "new-report") {
        composable("Landing") { LandingScreen(navController) }
        composable("signin") { SignInScreen(navController, authManager) }
        composable("signup") { SignUpScreen(navController, authManager) }
        composable("finish/{uid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
            FinishScreen(uid = uid, navController = navController, authManager = authManager)
        }
        composable("forgot") { ForgotPwdScreen(navController) }
        composable("profile/{uid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
            ProfileScreen(uid = uid, navController = navController, authManager = authManager)
        }
        composable("home") { MajorScreen(navController = navController) }
        composable("settings") { SettingsScreen(navController = navController) }
        composable("resources") { ResourcesScreen(navController = navController) }
        composable("notifications") { NotificationScreen(navController = navController) }
        composable("support") { SupportScreen(navController = navController) }
        composable("new-report") {
            val context = LocalContext.current
            ReportScreen(navController = navController, context = context)
        }
    }
}