package com.example.hushtrack

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hushtrack.ReportLogic.Report
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SuppressLint("ContextCastToActivity")
@Composable
fun AdminScreen(modifier: Modifier = Modifier, navController: NavController, uid: String, authManager: FireBaseAuthManager) {
    val activity = (LocalContext.current as? Activity)

    BackHandler {
        activity?.finish()
    }
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AdminDrawerContent(navController = navController)
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
                        Log.d("AdminScreen", "Navigating to profile for UID: $uid")
                        navController.navigate("profile/$uid")
                    }
                )
            }
        ) { padding ->
            AdminScreenContent(modifier = Modifier.padding(padding), navController = navController)
        }
    }
}

@Composable
fun AdminDrawerContent(modifier: Modifier = Modifier, navController: NavController) {
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
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider()

        Spacer(modifier = Modifier.height(5.dp))

        NavigationDrawerItem(
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
fun AdminScreenContent(modifier: Modifier = Modifier,navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()

    val filterOptions = listOf("All", "Pending Review", "Action Taken", "Resolved", "Dismissed")
    val scrollState = rememberScrollState()
    val reports = remember { mutableStateListOf<Report>() }
    var selectedFilter by remember { mutableStateOf("All") }
    val allReports by remember { mutableStateOf(listOf<Report>()) }

    val filteredReports = remember(selectedFilter, allReports) {
        if (selectedFilter == "All") allReports
        else allReports.filter { it.status == selectedFilter}
    }

    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Reports")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AdminScreen", "Firestore Error: ", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val fetchedReports = snapshot.documents.mapNotNull { doc ->
                        try {
                            val report = doc.toObject(Report::class.java)
                            report?.copy(id = doc.id)
                        } catch (e: Exception) {
                            Log.e("AdminScreen", "Data Parsing error: ", e)
                            null
                        }
                    }
                    reports.addAll(fetchedReports)

                    Log.d("AdminScreen", "Fetched ${fetchedReports.size} reports")
                } else {
                    Log.d("AdminScreen", "No reports Found")
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 100.dp)
    ) {
        Text(
            text = "Welcome Admin",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { filter ->
                val isSelected = filter == selectedFilter
                val buttonModifier = Modifier.padding(end = 8.dp)

                if (isSelected) {
                    Button(
                        onClick = { selectedFilter = filter },
                        shape = RoundedCornerShape(10.dp),
                        modifier = buttonModifier
                    ) {
                        Text(filter)
                    }
                } else {
                    OutlinedButton(
                    onClick = { selectedFilter = filter},
                    shape = RoundedCornerShape(10.dp),
//
                ) { Text(filter)}
                }
            }
//            filterOptions.forEach{ filter ->
//                OutlinedButton(
//                    onClick = {},
//                    shape = RoundedCornerShape(10.dp),
//
//                ) { Text(filter)}
//            }
        }

//        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
//            items(reports) { report ->
//                navController.navigate("manage-report/${report.id}/")
//            }
//        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(reports) { report ->
                val reportJson = Uri.encode(Gson().toJson(report))
                ReportCard(report = report) {
                    Log.d("AdminScreen", "Navigating to manage-report/${report.id}")
                    navController.navigate("manage-report/${report.id}")
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(
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
                Text(text = "HushTrack", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
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