package com.example.hushtrack

import android.media.MediaPlayer
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hushtrack.ReportLogic.Report
import com.example.hushtrack.utils.startPlayback
import com.example.hushtrack.utils.stopPlayback
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun manageReportScreen(navController: NavController, reportId: String/* report: Report */) {
//    Log.d("ManageReport", "View report with ID: $report.id")

    val firestore = FirebaseFirestore.getInstance()
    var report by remember { mutableStateOf<Report?>(null) }
    val context = LocalContext.current
    var selectedStatus by remember { mutableStateOf("Under Investigation") }
    val statusOptions = listOf("Pending Review", "Under Investigation", "Resolved", "Dismissed")
//    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var mediaPlayer = remember { MediaPlayer() }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(1) }
    var playbackPosition by remember { mutableStateOf(0) }
    val handler = Handler(Looper.getMainLooper())

    fun fetchReport() {
        isLoading = true
        loadError = null

        firestore.collection("Reports").document(reportId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fetchedReport = document.toObject(Report::class.java)?.copy(id = document.id)
                    report = fetchedReport
                    selectedStatus = fetchedReport?.status ?: ""
                    Log.d("ManageReportScreen", "Fetched Report: $fetchedReport")
                } else {
                    loadError = "No document found with ID: $reportId"
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                Log.e("ManageReportScreen", "Error fetching report", exception)
                loadError = exception.localizedMessage
                isLoading = false
            }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying && mediaPlayer != null) {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    val current = it.currentPosition
                    val total = it.duration.takeIf { d -> d > 0 } ?: 1
                    duration = total
                    progress = current.toFloat() / total.toFloat()
                }
            }
            delay(500)
        }
    }

    LaunchedEffect(reportId) {
        Log.d("ManageReportScreen", "Navigated with reportId: $reportId")
        fetchReport()
    }
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        loadError != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Failed to load report: $loadError")
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedButton(onClick = { fetchReport() }) {
                        Text("Retry")
                    }
                }
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "Manage Report",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        report?.let { InfoCard(title = "Reporter", value = it.reporter) }
                        report?.let { InfoCard(title = "Noise Type", value = it.noiseType) }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    report?.let { InfoBox(title = "Location", value = it.location) }

                    Spacer(modifier = Modifier.height(30.dp))
                    report?.let { InfoBox(title = "Report Decription", value = it.description) }

                    Spacer(modifier = Modifier.height(70.dp))

                    if (!report?.audioUrl.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "${playbackPosition / 1000}s",
                                modifier = Modifier.align(Alignment.End),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OutlinedButton(
                            onClick = {
//                                try {
//                                    Log.d("ManageReportScreen", "Playing audio: ${report?.audioUrl}")
//                                    mediaPlayer.reset()
//                                    mediaPlayer.setDataSource(report?.audioUrl ?: "")
//                                    mediaPlayer.prepare()
//                                    mediaPlayer.start()
//                                } catch (e: Exception) {
//                                    Log.e("ManageReportScreen", "Audio Playback Failed", e)
//                                }
//                                if (!isPlaying && report.audioUrl.isNotEmpty()) {
//                                    mediaPlayer = MediaPlayer().apply {
//                                        setDataSource(report?.audioUrl ?: "")
//                                        prepareAsync()
//                                        setOnPreparedListener{
//                                            start()
//                                            isPlaying = true
//                                        }
//                                    }
//                                }
                                try {
                                    Log.d("ManageReportScreen", "Playing Audio: ${report?.audioUrl}")
                                    mediaPlayer.reset()
                                   val decodedUrl = URLDecoder.decode(report?.audioUrl ?: "", StandardCharsets.UTF_8.toString())
                                    Log.d("ManageReportSCreen", "Decoded audio Url: $decodedUrl")
                                    mediaPlayer.setDataSource(decodedUrl)
                                    mediaPlayer.prepare()
                                    mediaPlayer.start()
                                    isPlaying = true
                                } catch (e: Exception) {
                                    Log.e("ManageReportScreen", "Audio Playback Failed", e)
                                    Toast.makeText(context, "Audio Playback Failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        ) {
                            Text("Play Recording")
                        }
                        OutlinedButton(
                            onClick = {
//                                if (mediaPlayer.isPlaying) {
//                                    mediaPlayer.stop()
//                                    Log.d("ManageReportScreen", "Stopped Ausio Playback")
//                                }
                                if (mediaPlayer.isPlaying) {
                                    mediaPlayer.stop()
                                    isPlaying = false
                                    Log.d("ManageReportScreen", "Stopped Audio Playback")
                                }
                            }
                        ) {
                            Text("Stop Playback")
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    Text("Report Status", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                    DropdownMenuBox(selectedStatus, statusOptions) {
                        selectedStatus = it
                        Log.d("ManageReportScreen", "Status selected: $it")
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    OutlinedButton(
                        onClick = {
//                        report?.let {
//
//                        }
                            report?.let {
                                firestore.collection("Reports").document(it.id)
                                    .update("status", selectedStatus)
                                    .addOnSuccessListener {
                                        Log.d("ManageReportScreen", "Status updated to $selectedStatus")
                                        Toast.makeText(context, "Status updated", Toast.LENGTH_LONG).show()
                                    }
                                    .addOnFailureListener { error ->
                                        Log.e("ManageReportScreen", "Failed to update Status", error)
                                    }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 30.dp)
                            .height(50.dp)
                    ) {
                        Text("Update Report", fontSize = 18.sp)
                    }
                }

            }
        }
    }



}

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
//            .weight(1f)
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(value)
        }
    }
}

@Composable
fun InfoBox(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(value)
        }
    }
}

@Composable
fun DropdownMenuBox(selectedOption: String, options: List<String>, onSelectionChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedOption, modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "DropDown Icon")
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false}, modifier = Modifier.fillMaxWidth()) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onSelectionChange(option)
                    expanded = false
                },
                    text = { Text(option)})
            }
        }
    }
}