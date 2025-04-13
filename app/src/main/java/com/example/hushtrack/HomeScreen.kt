package com.example.hushtrack

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hushtrack.ReportLogic.Report
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MajorScreen(modifier: Modifier = Modifier, navController: NavController, uid: String, authManager: FireBaseAuthManager) {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(navController = navController)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                    onProfileClick = {
                        Log.d("HomeScreen", "Navigating to profile for UID: $uid")
                        navController.navigate("profile/$uid")
                    }
                    )
            }
        ) { padding ->
            ScreenContent(modifier = Modifier.padding(padding), navController = navController, uid = uid, authManager = authManager)
        }
    }
}

@Composable
fun DrawerContent(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val packageInfo = remember {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    val versionName = packageInfo.versionName

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Text(
            text = "HushTrack",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        HorizontalDivider()

        Spacer(modifier = Modifier.height(5.dp))

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Account",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Settings",
                    fontSize = 18.sp,
                )
            },
            selected = false,
            onClick = {
                navController.navigate("settings")
            }
        )

        Spacer(modifier = Modifier.height(3.dp))

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Notifications",
                    fontSize = 18.sp,
                )
            },
            selected = false,
            onClick = {
                navController.navigate("notifications")
            }
        )
        Spacer(modifier = Modifier.height(3.dp))

        NavigationDrawerItem(

            label = {
                Text(
                    text = "Support",
                    fontSize = 18.sp,
                )
            },
            selected = false,
            onClick = {
                navController.navigate("support")
            }
        )

        Spacer(modifier = Modifier.height(3.dp))

        NavigationDrawerItem(
            label = {
                Text(
                    text = "Resources",
                    fontSize = 18.sp,
                )
            },
            selected = false,
            onClick = {
                navController.navigate("resources")
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Version $versionName",
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier, uid: String, authManager: FireBaseAuthManager, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }

    LaunchedEffect(Unit) {
        username = authManager.getUsername(uid) ?: "User"
        Log.d("HomeScreen", "Fetched username: $username")

//        For fetching reports
        FirebaseFirestore.getInstance()
            .collection("Reports")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                reports = snapshot.documents.mapNotNull { it.toObject(Report::class.java) }
            }
    }
//   Add the contents of the screen here
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 130.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Welcome $username",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedButton(
            onClick = {  navController.navigate("new-report/$uid") },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
                .height(51.dp),
        ) {
            Text(
                text = "New Report"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider()

        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No previous Reports",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            ReportList(reports = reports)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onOpenDrawer: () -> Unit, onProfileClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =  MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        ),
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(start = 16.dp, end = 15.dp)
                    .size(20.dp)
                    .clickable {
                        onOpenDrawer()
                    }

            )
        },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "HushTrack", textAlign = TextAlign.Center)
            }
        },
        actions = {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Accounts",
                modifier = Modifier
                    .padding(start = 8.dp, end = 15.dp)
                    .size(25.dp)
                    .clickable{
                        onProfileClick()
                    }

            )
        }
    )
}

@Composable
fun ReportList(reports: List<Report>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(reports) { report ->
            ReportCard(report)
        }
    }
}

@Composable
fun ReportCard(report: Report) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(report.formattedTimeStamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Text(
                    text = "Date: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = formattedDate,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Text(
                    text = "Status: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = report.status,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Text(
                    text = "location: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = report.location,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

