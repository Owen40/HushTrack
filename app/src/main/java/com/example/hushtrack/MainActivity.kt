package com.example.hushtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hushtrack.ui.theme.HushTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HushTrackTheme {
               AppNavigation()
//                ForgotPwdScreen()
//                ProfileScreen()
//                ClientScreen()
//                MajorScreen()
//                SupportScreen()
//                NotificationScreen()
//                ReportScreen()
//                ClientReportDescScreen()
//                EditProfileScreen()
//                AdminScreen()
//                manageReportScreen()
            }
        }
    }
}
