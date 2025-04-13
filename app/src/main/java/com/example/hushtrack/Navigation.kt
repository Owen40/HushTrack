package com.example.hushtrack

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hushtrack.ReportLogic.Report
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authManager = FireBaseAuthManager()

    var isCheckingAuth by remember { mutableStateOf(true) }
    var userType by remember { mutableStateOf<String?>(null) }
    var uid by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val userType = authManager.getUserType(uid!!)

            Log.d("Navigation", "User is logged in: $uid, UserType: $userType")

        }
        isCheckingAuth = false
    }

    if (isCheckingAuth) {
        LoadingScreen()
    } else {
        val startDestination = when {
            uid != null && userType != null -> if (userType == "admin") "admin/$uid" else "home/$uid"
            else -> "Landing"
        }
        NavHost(navController = navController, startDestination = startDestination) {
            composable("Landing") { LandingScreen(navController) }
            composable("signin") { SignInScreen(navController, authManager) }
            composable("signup") { SignUpScreen(navController, authManager) }
            composable("finish/{uid}") { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid")

                Log.d("Navigation", "Navigating to finish with UID: $uid")

                if (uid != null) {
                    FinishScreen(
                        uid = uid,
                        navController = navController,
                        authManager = authManager
                    )
                } else {
                    Log.e("Navigation", "Error: UID is null in FinishScreen")
                }
            }
            composable("forgot") { ForgotPwdScreen(navController) }
            composable("profile/{uid}") { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid")

                Log.d("Navigation", "Navigating to profile with UID: $uid")

                if (uid != null) {
                    ProfileScreen(
                        uid = uid,
                        navController = navController,
                        authManager = authManager
                    )
                } else {
                    Log.e("Navigation", "Error: UID is null in ProfileScreen")
                }
            }
            composable("Editprofile/{uid}") { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid")

                Log.d("Navigation", "Navigating to profile with UID: $uid")

                if (uid != null) {
                    EditProfileScreen(
                        uid = uid,
                        navController = navController,
                        authManager = authManager
                    )
                } else {
                    Log.e("Navigation", "Error: UID is null in ProfileScreen")
                }
            }

            composable("home/{uid}") { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
                MajorScreen(navController = navController, uid = uid, authManager = authManager)
            }
            composable("admin/{uid}") { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
                AdminScreen(navController = navController, uid = uid, authManager = authManager)
            }
            composable("settings") { SettingsScreen(navController = navController) }
            composable("resources") { ResourcesScreen(navController = navController) }
            composable("notifications") { NotificationScreen(navController = navController) }
            composable("support") { SupportScreen(navController = navController) }
            composable("new-report/{uid}") { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid")
                if (uid != null) {
                    ReportScreen(uid = uid, navController = navController, context = LocalContext.current, authManager = authManager)
                } else {
                    Log.e("Navigation", "Error: UID is null in ReportScreen")
                }
            }
            composable("new-report-details/{uid}/{downloadUrl}") { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
                val downloadUrl = backStackEntry.arguments?.getString("downloadUrl") ?: ""
                if (uid != null) {
                    ClientReportDescScreen(
                        uid = uid,
                        navController = navController,
                        context = LocalContext.current,
                        authManager = authManager,
                        downloadUrl = downloadUrl
                    )
                } else {
                    Log.e("Navigation", "Error: UID is null in ClientReportDescScreen")
                }
            }
//            composable(
//                "manage-report/{reportJson}",
//                arguments = listOf(navArgument("reportJson") { type = NavType.StringType })
//            ) { backStackEntry ->
////                val reportJson = backStackEntry.arguments?.getString("reportJson")
////                if (reportJson != null) {
////                    val decoded = URLDecoder.decode(reportJson, StandardCharsets.UTF_8.toString())
////                    val report = Gson().fromJson(decoded, Report::class.java)
////                    manageReportScreen(report = report, navController = navController)
////                } else {
////                    Log.e("Navigation", "reportJson is null")
////                }
//
////                val report = Gson().fromJson(URLDecoder.decode(reportJson, StandardCharsets.UTF_8.toString()), Report::class.java)
//
//                val reportJson = backStackEntry.arguments?.getString("reportJson")
//                val decoded = Uri.decode(reportJson)
//                val report = Gson().fromJson(decoded, Report::class.java)
//                manageReportScreen(navController = navController, report = report)
//            }
            composable("manage-report/{reportId}") { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
                if (reportId != null) {
                    manageReportScreen(navController = navController, reportId = reportId)
                } else (
                    Log.d("Navigation", "Error: reportId is null in manageReportScreen")
                )
            }
//            composable("new-report") {
//                val context = LocalContext.current
//                ReportScreen(navController = navController, context = context)
//            }
        }
    }
}